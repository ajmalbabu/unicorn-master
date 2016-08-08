package com.unicorn.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This service provides a common location for {@code Redis} related properties.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "unicorn.redis", ignoreUnknownFields = true)
public class RedisConfig {


    @Value("${unicorn.redis.host:localhost}")
    private String redisHost;

    @Value("${unicorn.redis.port:6379}")
    private int redisPort;

    @Value("${unicorn.redis.minIdleConnections:1}")
    private int minIdle;

    @Value("${unicorn.redis.maxIdleConnections:8}")
    private int maxIdle;

    @Value("${unicorn.redis.maxTotalConnections:8}")
    private int maxTotal;

    @Value("${redis.pub.database:0}")
    private String redisPubDatabase;

    @Value("${unicorn.redis.database:1}")
    private String redisInventoryDbName;

    @Value("${unicorn.redis.timeout:10000}")
    private long redisTimeout;


    public RedisConfig() {
    }

    public String getRedisConnection() {
        return "redis://" + getRedisHost() + ":" + getRedisPort();
    }


    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public String getRedisInventoryDbName() {
        return redisInventoryDbName;
    }


    public String getRedisPubSubDbName() {
        return redisPubDatabase;
    }


    public long getRedisTimeout() {
        return redisTimeout;
    }


    public String getHostAndPort() {
        return redisHost + ":" + redisPort;
    }


    @Override
    public String toString() {
        return "RedisConfig{" +
                ", redisHost='" + redisHost + '\'' +
                ", redisPort=" + redisPort +
                ", minIdle=" + minIdle +
                ", maxIdle=" + maxIdle +
                ", maxTotal=" + maxTotal +
                ", redisPubDatabase='" + redisPubDatabase + '\'' +
                ", redisInventoryDbName='" + redisInventoryDbName + '\'' +
                ", redisTimeout=" + redisTimeout +
                '}';
    }
}
