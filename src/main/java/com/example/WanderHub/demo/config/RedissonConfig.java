package com.example.WanderHub.demo.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // Configuration for the Redis server
        Config config = new Config();

        // Here, specify the Redis server address (could be localhost or another Redis server)
        config.useSingleServer().setAddress("redis://localhost:6379"); // Adjust address accordingly

        // Return the Redisson client configured
        return Redisson.create(config);
    }
}

