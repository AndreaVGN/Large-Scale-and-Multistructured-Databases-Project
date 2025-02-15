package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import io.lettuce.core.ReadFrom;

@Configuration
public class RedisConfig {

    // Configura la connessione a Redis Cluster
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Configurazione del cluster con i nodi del cluster Redis
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration()
                .clusterNode("10.1.1.9", 7000)
                .clusterNode("10.1.1.9", 7001)
                .clusterNode("10.1.1.71", 7000)
                .clusterNode("10.1.1.71", 7001)
                .clusterNode("10.1.1.74", 7000)
                .clusterNode("10.1.1.74", 7001);

        // Restituisce la connessione a Lettuce per Redis Cluster
        return new LettuceConnectionFactory(clusterConfig);
    }

    // Configura il template Redis utilizzando la connessione a Redis Cluster
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
