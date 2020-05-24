package com.github.mjd507.registercenter.client.entity;

import lombok.Data;

/**
 * 服务注册实体
 * Created by mjd on 2020/5/23 13:10
 */
@Data
public class RegisterEntity {
    private String serviceName; // 服务名称
    private String host; // 主机名
    private String ip; // ip
    private int status; // 服务状态
}
