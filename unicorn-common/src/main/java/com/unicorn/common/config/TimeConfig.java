package com.unicorn.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TimeConfig is populated yaml files. It contains TimeItems which makes it
 * easy for us to describe time measurements in a readable manner.
 */
@Component
@ConfigurationProperties(prefix = "time", ignoreUnknownFields = true)
public class TimeConfig {

    private TimeItem valuationDelay;
    private TimeItem cassandraTimeout;
    private TimeItem redisExpire;
    private TimeItem redisTimeout;
    private TimeItem redisSyncTimeout;
    private TimeItem vastInventoryTTL;


    public TimeItem getValuationDelay() {
        return valuationDelay;
    }

    public void setValuationDelay(TimeItem valuationDelay) {
        this.valuationDelay = valuationDelay;
    }

    public TimeItem getCassandraTimeout() {
        return cassandraTimeout;
    }

    public void setCassandraTimeout(TimeItem cassandraTimeout) {
        this.cassandraTimeout = cassandraTimeout;
    }

    public TimeItem getRedisExpire() {
        return redisExpire;
    }

    public void setRedisExpire(TimeItem redisExpire) {
        this.redisExpire = redisExpire;
    }

    public TimeItem getRedisTimeout() {
        return redisTimeout;
    }

    public void setRedisTimeout(TimeItem redisTimeout) {
        this.redisTimeout = redisTimeout;
    }

    public TimeItem getRedisSyncTimeout() {
        return redisSyncTimeout;
    }

    public void setRedisSyncTimeout(TimeItem redisSyncTimeout) {
        this.redisSyncTimeout = redisSyncTimeout;
    }

    public TimeItem getVastInventoryTTL() {
        return vastInventoryTTL;
    }

    public void setVastInventoryTTL(TimeItem vastInventoryTTL) {
        this.vastInventoryTTL = vastInventoryTTL;
    }
}
