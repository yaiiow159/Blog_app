package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.*;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    @Bean(name = "defaultThreadPoolExecutor")
    public ThreadPoolExecutor executor() {
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE + 1,
                CORE_POOL_SIZE * 2,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50));
        // 執行續資源被耗盡 轉為 交給主線程執行任務
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "mailThreadPoolExecutor")
    public ThreadPoolExecutor mailThreadPoolExecutor() {
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                 CORE_POOL_SIZE + 1,
                CORE_POOL_SIZE * 2,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50));
        // 執行續資源被耗盡 轉為 交給主線程執行任務
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "noResubmitThreadPoolExecutor")
    public ThreadPoolExecutor noResubmitExecutor() {
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE + 1,
                CORE_POOL_SIZE * 2,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        // 執行續資源被耗盡 轉為 交給主線程執行任務
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

}
