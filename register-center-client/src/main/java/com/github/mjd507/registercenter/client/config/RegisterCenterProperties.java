package com.github.mjd507.registercenter.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by mjd on 2020/4/20 08:57
 */
@ConfigurationProperties(prefix = "registercenter")
@Data
public class RegisterCenterProperties {

    private String connectString;
    private String serviceName;
    private String discoveryServiceName;

}
