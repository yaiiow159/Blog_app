package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;

/**
 * redission 配置類
 */
@ConfigurationProperties(prefix = "redisson.config")
@Configuration
@Slf4j
public class RedissionConfig {

    private String address;
    private String password;
    private int database;

    @Bean(name = "redissonConnectionFactory")
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }
    // redisson 配置
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisClient() {
        Config config = new Config();
        SingleServerConfig sconfig= config.useSingleServer()
                .setAddress(address)
                .setDatabase(database);
        if(StringUtils.hasText(password)){
            sconfig.setPassword(password);
        }
        return Redisson.create(config);
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getDatabase() {
        return database;
    }
}
