package com.example.easychat.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {
    @Value("${spring.redis.host:}")
    private String redisHost;

    @Value("${spring.redis.port:}")
    private Integer redisPort;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(name = "spring.redis.host")
    public RedissonClient redissonClient() {
        Config config = new Config();
        try {
            config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
            return Redisson.create(config);
        } catch (Exception e) {
            System.out.println("Redis 配置错误：" + e.getMessage());
            return null;
        }
    }


    @Bean("redisTemplate")
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory); //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string()); //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json()); //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string()); //设置hash的 value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();

        return template;
    }

}
