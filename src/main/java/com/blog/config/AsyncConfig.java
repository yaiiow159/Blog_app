package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    @Bean(name = "defaultThreadPoolExecutor")
    public ThreadPoolExecutor executor() {
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                2 * CORE_POOL_SIZE + 1,
                CORE_POOL_SIZE * 5,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        // 執行續資源被耗盡 轉為 交給主線程執行任務
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "mailExecutor")
    public ThreadPoolExecutor mailExecutor() {
        return new ThreadPoolExecutor(
                5,
                20,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

}
