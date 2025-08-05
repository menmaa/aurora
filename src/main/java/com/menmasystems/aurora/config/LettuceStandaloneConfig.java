/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.config;

import com.menmasystems.aurora.redis.RedisBloomCommands;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LettuceStandaloneConfig implements DisposableBean {
    private RedisClient client;
    private StatefulRedisConnection<String,String> connection;
    private final RedisProperties redisProperties;

    public LettuceStandaloneConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public RedisClient redisClient() {
        this.client = RedisClient.create(getRedisURI());
        return client;
    }

    @Bean
    public StatefulRedisConnection<String,String> statefulRedisConnection(RedisClient redisClient) {
        this.connection = redisClient.connect();
        return connection;
    }

    @Bean
    public RedisBloomCommands redisBloomCommands(StatefulRedisConnection<String,String> connection) {
        return new RedisCommandFactory(connection).getCommands(RedisBloomCommands.class);
    }

    private RedisURI getRedisURI() {
        if(redisProperties.getUrl() != null) {
            return RedisURI.create(redisProperties.getUrl());
        }

        RedisURI.Builder builder = RedisURI.builder()
                .withSsl(redisProperties.getSsl().isEnabled())
                .withHost(redisProperties.getHost())
                .withPort(redisProperties.getPort());

        return builder.build();
    }

    @Override
    public void destroy() throws Exception {
        if(connection != null) {
            connection.close();
        }

        if(client != null) {
            client.shutdown();
        }
    }
}
