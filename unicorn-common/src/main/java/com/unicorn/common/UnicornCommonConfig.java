package com.unicorn.common;

import com.google.common.cache.CacheBuilder;
import com.unicorn.common.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Creates the core spring beans needed for unicorn-common module.
 * </p>
 */
@Configuration
public class UnicornCommonConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnicornCommonConfig.class);

    @Value("${isolate.mode:false}")
    private boolean isolateMode;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    @Qualifier(value = "primaryRedisTemplate")
    private RedisTemplate primaryRedisTemplate;

    @Bean(name = "primaryJedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig());
        factory.setHostName(redisConfig.getRedisHost());
        factory.setPort(redisConfig.getRedisPort());
        factory.setTimeout((int) redisConfig.getRedisTimeout());
        factory.setUsePool(true);
        return factory;
    }

    @Bean(name = "primaryJedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMinIdle(redisConfig.getMinIdle());
        jedisPoolConfig.setMaxIdle(redisConfig.getMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getMaxTotal());

        return jedisPoolConfig;
    }

    @Bean(name = "primaryRedisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }


    @Bean
    public CacheManager sessionCacheManager() {
        if (isolateMode) {
            LOGGER.info("Using in-memory cache.");
            return inMemoryCacheManager();
        } else {
            LOGGER.info("Using REDIS cache.");
            return new RedisCacheManager(primaryRedisTemplate);
        }
    }

    /**
     * <p>
     * In-memory session cache manager. Useful during unit-testing and work station (laptop) development
     * without having REDIS as the session cache manager dependency.
     * </p>
     *
     * @return The CacheManager that will be used.
     */
    private CacheManager inMemoryCacheManager() {

        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.HOURS).maximumSize(Long.MAX_VALUE));
        return cacheManager;
    }
}
