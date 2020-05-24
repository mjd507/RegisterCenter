package com.github.mjd507.registercenter.client.core;

import com.github.mjd507.registercenter.client.entity.RegisterEntity;
import com.github.mjd507.registercenter.client.util.NodePathUtil;
import com.github.mjd507.registercenter.client.util.ServiceStatus;
import com.github.mjd507.util.exception.BusinessException;
import com.github.mjd507.util.util.HostUtil;
import com.github.mjd507.util.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 服务注册
 * Created by mjd on 2020/5/23 18:27
 */
public class ServiceRegistry {

    /* 服务注册到 zk */
    public static void register(CuratorManager curatorManager, String serviceName) {
        if (StringUtils.isBlank(serviceName)) throw new BusinessException("服务注册时，名称必须提供，一般为项目名，需唯一");
        RegisterEntity entity = new RegisterEntity();
        entity.setServiceName(serviceName);
        entity.setHost(HostUtil.getHostName());
        entity.setIp(HostUtil.getHostIp());
        entity.setStatus(ServiceStatus.normal);
        curatorManager.register(NodePathUtil.servicesProviderPrefixWrapper(serviceName, entity.getHost()), JsonUtil.toJsonStr(entity));
    }

}
