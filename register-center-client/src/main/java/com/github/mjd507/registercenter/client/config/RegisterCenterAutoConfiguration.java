package com.github.mjd507.registercenter.client.config;

/**
 * Created by mjd on 2020/4/20 08:59
 */

import com.github.mjd507.registercenter.client.core.CuratorManager;
import com.github.mjd507.registercenter.client.core.ServiceDiscovery;
import com.github.mjd507.registercenter.client.core.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(RegisterCenterProperties.class)
@EnableConfigurationProperties(RegisterCenterProperties.class)
public class RegisterCenterAutoConfiguration {

    @Bean(name = "registerCenterCuratorManager", destroyMethod = "close")
    public CuratorManager curatorManager(RegisterCenterProperties registerCenterProperties) {
        CuratorManager curatorManager = new CuratorManager();
        String serviceName = registerCenterProperties.getServiceName();
        String discoveryServiceName = registerCenterProperties.getDiscoveryServiceName();
        String connectString = registerCenterProperties.getConnectString();
        curatorManager.setConnectString(connectString);
        curatorManager.init();
        ServiceRegistry.register(curatorManager, serviceName);
        ServiceDiscovery.discovery(curatorManager, discoveryServiceName);
        return curatorManager;
    }

}
