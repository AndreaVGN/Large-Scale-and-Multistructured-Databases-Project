package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Configura Redis Sentinel con Lettuce
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")  // Nome del master definito in Redis Sentinel
                .sentinel("10.1.1.9", 26379)  // Indirizzo e porta di uno dei Sentinel
                .sentinel("10.1.1.71", 26379) // Indirizzo e porta di un altro Sentinel
                .sentinel("10.1.1.74", 26379); // Terzo Sentinel, se lo hai

        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
