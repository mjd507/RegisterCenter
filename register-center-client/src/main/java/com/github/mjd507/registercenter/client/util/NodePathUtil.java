package com.github.mjd507.registercenter.client.util;

import com.github.mjd507.util.exception.BusinessException;

/**
 * Created by mjd on 2020/5/23 18:28
 */
public class NodePathUtil {

    /**
     * 服务提供者写入 zk 路径
     * /services/app1/host1xxx
     * /services/app2/host2xxx
     */
    public static String servicesProviderPrefixWrapper(String serviceName, String itemKey) {
        return "/services" + fixPath(serviceName) + fixPath(itemKey);
    }

    /**
     * 服务发现者监听 zk 路径
     * /services/app1
     * /services/app2
     */
    public static String servicesDiscoveryWrapper(String serviceName) {
        return "/services" + fixPath(serviceName);
    }

    public static String serviceDiscoveryHostName(String nodePath) {
        return nodePath.substring(nodePath.lastIndexOf("/"));
    }

    private static String fixPath(String key) {
        if (key == null) throw new BusinessException("节点 key 不可为 null");
        if (!key.startsWith("/")) key = "/" + key;
        if (key.endsWith("/") && key.length() > 1) key = key.substring(0, key.length() - 1);
        return key;
    }

}
