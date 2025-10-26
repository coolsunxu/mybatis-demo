package com.example.mybatisdemo.config.beans;


import com.example.mybatisdemo.config.props.ThreadPoolConfig;
import com.example.mybatisdemo.threadpool.TimingThreadPool;
import com.example.mybatisdemo.util.ThreadFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */
@Configuration
@Slf4j
public class ExecutorConfiguration {

    private final ThreadPoolConfig threadPoolConfig;

    public ExecutorConfiguration(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }

    @Bean(name = "fastTaskExecutor", destroyMethod = "shutdown")
    public TimingThreadPool asyncExecutor() {
        return createExecutor(
                threadPoolConfig.getFast().getCorePoolSize(),
                threadPoolConfig.getFast().getMaxPoolSize(),
                threadPoolConfig.getFast().getKeepAlive(),
                threadPoolConfig.getFast().getQueueCapacity(),
                threadPoolConfig.getFast().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "slowTaskExecutor", destroyMethod = "shutdown")
    public TimingThreadPool slowTaskExecutor() {
        return createExecutor(
                threadPoolConfig.getSlow().getCorePoolSize(),
                threadPoolConfig.getSlow().getMaxPoolSize(),
                threadPoolConfig.getSlow().getKeepAlive(),
                threadPoolConfig.getSlow().getQueueCapacity(),
                threadPoolConfig.getSlow().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "delayTaskExecutor", destroyMethod = "shutdown")
    public TimingThreadPool delayTaskExecutor() {
        return createExecutor(
                threadPoolConfig.getSlow().getCorePoolSize(),
                threadPoolConfig.getSlow().getMaxPoolSize(),
                threadPoolConfig.getSlow().getKeepAlive(),
                threadPoolConfig.getSlow().getQueueCapacity(),
                threadPoolConfig.getSlow().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private TimingThreadPool createExecutor(
            int defCorePoolSize,
            int defMaxPoolSize,
            int defKeepAlive,
            int defQueueCapacity,
            String threadNamePrefix,
            RejectedExecutionHandler rejectedExecutionHandler
    ) {

        return new TimingThreadPool(
                defCorePoolSize,
                defMaxPoolSize,
                defKeepAlive,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(defQueueCapacity),
                ThreadFactoryUtil.create(threadNamePrefix),
                rejectedExecutionHandler
        );
    }

}

