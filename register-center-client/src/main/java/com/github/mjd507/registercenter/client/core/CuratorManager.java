package com.github.mjd507.registercenter.client.core;

import com.github.mjd507.util.exception.BusinessException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 curator 管理 zookeeper
 * <p>
 * Created by mjd on 2020/4/12 15:35
 */
public class CuratorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorManager.class);

    private String connectString;
    private int sessionTimeoutMs = 60 * 1000;
    private int connectionTimeoutMs = 15 * 1000;
    private RetryPolicy retryPolicy;

    private CuratorFramework curatorClient;

    private static final List<PathChildrenCache> pathChildCaches = new ArrayList<>();

    public static void addPathChildCache(PathChildrenCache pathChildrenCache) {
        pathChildCaches.add(pathChildrenCache);
    }

    public void init() {
        if (connectString == null) {
            throw new BusinessException("zookeeper 集群地址不能为空");
        }
        if (retryPolicy == null) {
            retryPolicy = new ExponentialBackoffRetry(1000, 3);
        }
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .build();
        curatorClient.start();
    }

    public void close() {
        for (PathChildrenCache pathChildCache : pathChildCaches) {
            try {
                pathChildCache.close();
            } catch (IOException e) {
                LOGGER.error("zookeeper watch listener close exception", e);
            }
        }
        if (curatorClient != null) {
            curatorClient.close();
        }
    }


    // crud operation

    public boolean register(String key, String value) {
        try {
            curatorClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(key, value.getBytes());
        } catch (Exception e) {
            LOGGER.error("!!! 服务注册失败，key:{}, value:{}.", key, value, e);
            return false;
        }
        return true;
    }

    public boolean unregister(String key) {
        try {
            curatorClient.delete().forPath(key);
        } catch (Exception e) {
            LOGGER.error("!!! 下线失败，key:{}.", key, e);
            return false;
        }
        return true;
    }

    public boolean update(String key, String newVal) {
        try {
            curatorClient.setData().forPath(key, newVal.getBytes());
        } catch (Exception e) {
            LOGGER.error("!!! 更新失败，key:{}, value:{}.", key, newVal, e);
            return false;
        }
        return true;
    }


    public void watch(String key, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorClient, key, true);
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener(listener);
        addPathChildCache(pathChildrenCache);
    }


    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

}
