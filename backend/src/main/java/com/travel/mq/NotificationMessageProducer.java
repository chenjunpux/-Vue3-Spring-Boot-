package com.travel.mq;

import com.travel.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 通知消息生产者
 * 
 * 用于发送各类通知消息：
 * 1. 订单支付成功通知
 * 2. 订单取消通知
 * 3. 退款通知
 * 4. 游记评论通知
 * 5. 系统公告通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送订单通知
     */
    public void sendOrderNotification(Long userId, String orderNo, String type, String title, String content) {
        log.info("发送订单通知: userId={}, orderNo={}, type={}", userId, orderNo, type);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.create",
                new NotificationMessage(userId, type, title, content, "/orders/" + orderNo)
        );
    }

    /**
     * 发送游记通知
     */
    public void sendArticleNotification(Long userId, Long articleId, String type, String title, String content) {
        log.info("发送游记通知: userId={}, articleId={}, type={}", userId, articleId, type);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.create",
                new NotificationMessage(userId, type, title, content, "/articles/" + articleId)
        );
    }

    /**
     * 发送系统通知
     */
    public void sendSystemNotification(String type, String title, String content, String linkUrl) {
        log.info("发送系统通知: type={}, title={}", type, title);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.create",
                new NotificationMessage(null, type, title, content, linkUrl)
        );
    }

    /**
     * 通知消息实体
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class NotificationMessage {
        private Long userId;           // 接收用户ID，null表示全体用户
        private String type;           // 通知类型
        private String title;          // 通知标题
        private String content;        // 通知内容
        private String linkUrl;        // 跳转链接
    }
}
