package com.travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring Retry 重试机制配置
 * 
 * 用于以下场景：
 * 1. 支付回调失败重试
 * 2. 远程服务调用失败重试
 * 3. 数据库操作失败重试
 * 4. 消息发送失败重试
 * 
 * 使用方式：
 * @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
 */
@Configuration
@EnableRetry
public class RetryConfig {
    // Spring Retry 默认配置已足够，无需额外配置
    // 如需自定义，可在 application.yml 中配置 spring.retry.*
}
