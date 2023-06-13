package com.czx.gogo.confiig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyConfig {
    /*
  此处成员变量应该使用@Value从配置中读取
   */
    private int corePoolSize = 10;
    private int maxPoolSize = 200;
    private int KeepAliveSeconds = 5;
    private int queueCapacity = 10;

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //允许线程最大空闲时间、默认为秒
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        //缓存队列大小
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        /**
         * taskDecorator主要是对Runnable任务装饰一下， 在任务执行时完成异常日志打印、ThreadLocal清理等功能
         * 但是对Callable任务（由submit（）方法提交的任务），这个taskDecorator虽然也能装饰，但是并不能捕获异常，
         * 因为类似FutureTask的run方法内部自己补获了异常，不会抛出到afterExecute方法中
         */
        // 增加 TaskDecorator 属性的配置，解决多线程场景下，获取不到request上下文的问题
//        executor.setTaskDecorator(new MyDecorator());
        executor.initialize();
        return executor;
    }
}
