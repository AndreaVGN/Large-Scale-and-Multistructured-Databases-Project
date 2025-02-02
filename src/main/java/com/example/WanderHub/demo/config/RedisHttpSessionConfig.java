package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisHttpSessionConfig {
    // Puoi aggiungere qui eventuali bean, come RedisConnectionFactory, se necessario.
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Configura JedisConnectionFactory con host e porta specificati
        return new JedisConnectionFactory();
    }
}
