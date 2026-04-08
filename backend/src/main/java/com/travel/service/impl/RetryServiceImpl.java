package com.travel.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * 重试服务
 * 
 * Spring Retry 实现，用于以下场景：
 * 1. 网络请求失败重试
 * 2. 数据库操作失败重试
 * 3. 第三方支付回调重试
 * 
 * 使用方式：
 * 在需要重试的方法上添加 @Retryable 注解
 * 在重试后的回调方法上添加 @Recover 注解
 */
@Slf4j
@Service
public class RetryServiceImpl {

    /**
     * 支付回调重试
     * 
     * @param orderNo 订单号
     * @param channel 支付渠道
     * @return 是否成功
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public boolean retryPaymentCallback(String orderNo, String channel) {
        log.info("执行支付回调重试: orderNo={}, channel={}", orderNo, channel);
        // 模拟支付回调处理
        // 实际应该调用 PaymentService.processCallback(orderNo, channel)
        return true;
    }

    @Recover
    public boolean recoverPaymentCallback(Exception e, String orderNo, String channel) {
        log.error("支付回调重试失败，已达最大次数: orderNo={}, channel={}", orderNo, channel, e);
        // 记录日志，发送告警通知
        return false;
    }

    /**
     * 发送通知重试
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public boolean retrySendNotification(Long userId, String title, String content) {
        log.info("执行通知发送重试: userId={}, title={}", userId, title);
        // 实际应该调用 NotificationService.sendNotification(userId, title, content)
        return true;
    }

    @Recover
    public boolean recoverSendNotification(Exception e, Long userId, String title, String content) {
        log.error("通知发送重试失败，已达最大次数: userId={}, title={}", userId, title, e);
        return false;
    }

    /**
     * 更新搜索索引重试
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public boolean retryUpdateSearchIndex(Long targetId, String targetType) {
        log.info("执行搜索索引更新重试: targetId={}, targetType={}", targetId, targetType);
        // 实际应该调用 SearchService.updateIndex(targetId, targetType)
        return true;
    }

    @Recover
    public boolean recoverUpdateSearchIndex(Exception e, Long targetId, String targetType) {
        log.error("搜索索引更新重试失败: targetId={}, targetType={}", targetId, targetType, e);
        return false;
    }

    /**
     * 发送邮件重试
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    public boolean retrySendEmail(String to, String subject, String content) {
        log.info("执行邮件发送重试: to={}, subject={}", to, subject);
        // 实际应该调用 EmailService.send(to, subject, content)
        return true;
    }

    @Recover
    public boolean recoverSendEmail(Exception e, String to, String subject, String content) {
        log.error("邮件发送重试失败: to={}, subject={}", to, subject, e);
        return false;
    }

    /**
     * 通用重试方法
     * 
     * @param action 要执行的操作
     * @param maxAttempts 最大重试次数
     * @param delayMs 初始延迟（毫秒）
     */
    public void retry(Runnable action, int maxAttempts, long delayMs) {
        int attempt = 0;
        long currentDelay = delayMs;
        
        while (attempt < maxAttempts) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    log.error("重试已达最大次数: maxAttempts={}", maxAttempts, e);
                    throw e;
                }
                log.warn("执行失败，准备第{}次重试: delay={}ms", attempt + 1, currentDelay);
                try {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
                currentDelay *= 2; // 指数退避
            }
        }
    }
}
