package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

import java.time.Duration;

@Configuration
public class RedisConfig {


    @Bean
    public RedisConnectionFactory redisConnectionFactoryMasterPreferred() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("10.1.1.9", 26379)
                .sentinel("10.1.1.71", 26379)
                .sentinel("10.1.1.74", 26379);

        LettuceClientConfiguration lettuceClientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.MASTER_PREFERRED)  // Lettura dal master preferito
                .build();

        return new LettuceConnectionFactory(sentinelConfig, lettuceClientConfig);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryReplicaPreferred() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("10.1.1.9", 26379)
                .sentinel("10.1.1.71", 26379)
                .sentinel("10.1.1.74", 26379);

        LettuceClientConfiguration lettuceClientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)  // Lettura dalla replica preferita
                .build();


        return new LettuceConnectionFactory(sentinelConfig, lettuceClientConfig);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryNearest() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("10.1.1.9", 26379)
                .sentinel("10.1.1.71", 26379)
                .sentinel("10.1.1.74", 26379);

        LettuceClientConfiguration lettuceClientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.NEAREST)  // Lettura dal nodo più vicino
                .build();

        return new LettuceConnectionFactory(sentinelConfig, lettuceClientConfig);
    }

    @Primary
    @Bean
    public RedisConnectionFactory redisConnectionFactoryMaster() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("10.1.1.9", 26379)
                .sentinel("10.1.1.71", 26379)
                .sentinel("10.1.1.74", 26379);


        LettuceClientConfiguration lettuceClientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.MASTER)
                .build();


        return new LettuceConnectionFactory(sentinelConfig, lettuceClientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateMasterPreferred(RedisConnectionFactory redisConnectionFactoryMasterPreferred) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryMasterPreferred);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateReplica(RedisConnectionFactory redisConnectionFactoryReplicaPreferred) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryReplicaPreferred);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateNearest(RedisConnectionFactory redisConnectionFactoryNearest) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryNearest);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactoryMaster) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryMaster);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}

