/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.configurations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author cmegafu
 */
@Configuration
public class EmailConfig {
    
    @Value("${app.threadPoolSize}")
    private int threadPoolSize;
    
    @Bean
    public ExecutorService executorService() {
//        return ForkJoinPool.commonPool();
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
