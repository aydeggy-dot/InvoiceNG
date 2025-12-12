package com.invoiceng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration to enable async processing for webhook handling.
 * This ensures @Async methods run in Spring-managed threads with proper
 * transaction context support.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
