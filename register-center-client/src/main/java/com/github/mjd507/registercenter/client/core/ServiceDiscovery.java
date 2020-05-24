package com.github.mjd507.registercenter.client.core;

import com.github.mjd507.registercenter.client.util.NodePathUtil;
import com.github.mjd507.util.util.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;

/**
 * 服务发现
 * Created by mjd on 2020/5/23 18:27
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private static final Map<String, Object> serviceHostMap = MapUtil.newMap();

    /* 监听 zk */
    public static void discovery(CuratorManager curatorManager, String discoveryServiceName) {
        if (StringUtils.isBlank(discoveryServiceName)) return;
        try {
            curatorManager.watch(NodePathUtil.servicesDiscoveryWrapper(discoveryServiceName), (client, event) -> {
                if (event == null || event.getData() == null) return;
                String changePath = event.getData().getPath();
                String newVal = null;

                if (event.getType() != CHILD_REMOVED) {
                    try {
                        newVal = new String(client.getData().forPath(changePath));
                    } catch (Exception ignore) {
                    }
                }
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        updateService(NodePathUtil.serviceDiscoveryHostName(changePath), newVal);
                        break;
                    case CHILD_REMOVED:
                        updateService(NodePathUtil.serviceDiscoveryHostName(changePath), null);
                        break;
                    default:
                        break;
                }
                checkServices();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateService(String host, String val) {
        if (val == null) serviceHostMap.remove(host);
        else serviceHostMap.put(host, val);
    }

    public static Map<String, Object> getAllServices() {
        return serviceHostMap;
    }

    public static void checkServices() {
        if (serviceHostMap.isEmpty()) {
            LOGGER.info("当前没有服务提供者，请检查");
        }
        for (Map.Entry<String, Object> entry : serviceHostMap.entrySet()) {
            String host = entry.getKey();
            Object value = entry.getValue();
            LOGGER.info("服务提供者，名称:{}, 信息:{}", host, value);
        }
    }

}
