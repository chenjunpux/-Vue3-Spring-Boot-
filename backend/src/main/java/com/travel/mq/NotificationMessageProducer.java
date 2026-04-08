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
     * 发送系统通知给全部用户（批量）
     */
    public void sendNotificationAll(Long notificationId, String title, String content, Integer type) {
        log.info("发送系统通知给全部用户: notificationId={}, title={}", notificationId, title);
        NotificationMessage msg = new NotificationMessage();
        msg.setUserId(-1L); // -1 表示全部用户
        msg.setType(String.valueOf(type));
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLinkUrl("/notifications");
        msg.setNotificationId(notificationId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.create",
                msg
        );
    }

    /**
     * 发送系统通知给指定用户
     */
    public void sendNotificationBatch(Long notificationId, String title, String content, Integer type, String userIdsJson) {
        log.info("发送系统通知给指定用户: notificationId={}, title={}", notificationId, title);
        NotificationMessage msg = new NotificationMessage();
        msg.setUserId(-2L); // -2 表示指定用户
        msg.setType(String.valueOf(type));
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLinkUrl("/notifications");
        msg.setNotificationId(notificationId);
        msg.setUserIdsJson(userIdsJson);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.create",
                msg
        );
    }

    /**
     * 通知消息实体
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class NotificationMessage {
        private Long userId;           // 接收用户ID，null表示全体用户，-1全部用户，-2指定用户
        private String type;           // 通知类型
        private String title;          // 通知标题
        private String content;        // 通知内容
        private String linkUrl;        // 跳转链接
        private Long notificationId;    // 系统通知ID
        private String userIdsJson;    // 指定用户ID列表JSON

        // 兼容原有 5 参数构造器
        public NotificationMessage(Long userId, String type, String title, String content, String linkUrl) {
            this.userId = userId;
            this.type = type;
            this.title = title;
            this.content = content;
            this.linkUrl = linkUrl;
        }
    }
}
