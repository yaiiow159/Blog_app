package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    @Bean(name = "defaultThreadPoolExecutor")
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE * 2 + 1);
        taskExecutor.setMaxPoolSize(CORE_POOL_SIZE * 4 + 1);
        taskExecutor.setQueueCapacity(50);
        // 設置 非核心線程存活時間
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("default-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "mailThreadPoolExecutor")
    public ThreadPoolTaskExecutor mailThreadPoolExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE + 1);
        taskExecutor.setMaxPoolSize(CORE_POOL_SIZE * 2 + 1);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setKeepAliveSeconds(300);
        taskExecutor.setThreadNamePrefix("mail-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "noResubmitThreadPoolExecutor")
    public ThreadPoolTaskExecutor noResubmitExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE * 2 + 1);
        taskExecutor.setMaxPoolSize(CORE_POOL_SIZE * 4 + 1);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setKeepAliveSeconds(30);
        taskExecutor.setThreadNamePrefix("noResubmit-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

}
