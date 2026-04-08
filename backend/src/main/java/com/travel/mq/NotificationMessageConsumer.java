package com.travel.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import com.travel.config.RabbitMQConfig;
import com.travel.entity.Notification;
import com.travel.entity.User;
import com.travel.mapper.NotificationMapper;
import com.travel.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知消息消费者
 * 
 * 消费通知相关的异步消息：
 * 1. 系统通知发送给全部用户
 * 2. 系统通知发送给指定用户
 * 3. 订单/活动等业务通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageConsumer {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 处理通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(
            NotificationMessageProducer.NotificationMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("收到通知消息: userId={}, title={}", message.getUserId(), message.getTitle());

            if (message.getUserId() != null && message.getUserId() == -1L) {
                // 发送给全部用户
                handleSendToAll(message);
            } else if (message.getUserId() != null && message.getUserId() == -2L) {
                // 发送给指定用户
                handleSendToBatch(message);
            } else if (message.getUserId() != null) {
                // 单个用户
                sendToUser(message.getUserId(), message.getType(), message.getTitle(), message.getContent(), message.getLinkUrl());
            } else {
                // 没有指定用户，默认行为：查全部
                handleSendToAll(message);
            }

            channel.basicAck(deliveryTag, false);
            log.info("通知消息处理成功");
        } catch (Exception e) {
            log.error("处理通知消息失败", e);
            // 失败不 ack，消息会重新入队
            channel.basicNack(deliveryTag, false, true);
        }
    }

    /**
     * 发送给全部用户（分批处理，避免一次查询太多）
     */
    private void handleSendToAll(NotificationMessageProducer.NotificationMessage message) {
        // 每次处理 100 条，避免内存溢出
        int batchSize = 100;
        int offset = 0;

        while (true) {
            List<User> users = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .select(User::getId)
                            .last("LIMIT " + offset + ", " + batchSize)
            );

            if (users.isEmpty()) break;

            for (User user : users) {
                sendToUser(user.getId(), message.getType(), message.getTitle(), message.getContent(), message.getLinkUrl());
            }

            offset += batchSize;
            if (users.size() < batchSize) break;
        }
    }

    /**
     * 发送给指定用户
     */
    private void handleSendToBatch(NotificationMessageProducer.NotificationMessage message) {
        if (message.getUserIdsJson() == null || message.getUserIdsJson().isBlank()) {
            handleSendToAll(message);
            return;
        }

        try {
            // JSON 格式: [1,2,3] 或 "1,2,3"
            String ids = message.getUserIdsJson().replaceAll("[\\[\\]\"]", "");
            String[] idArr = ids.split(",");

            for (String idStr : idArr) {
                Long userId = Long.parseLong(idStr.trim());
                sendToUser(userId, message.getType(), message.getTitle(), message.getContent(), message.getLinkUrl());
            }
        } catch (Exception e) {
            log.error("解析指定用户ID失败: {}", message.getUserIdsJson(), e);
        }
    }

    /**
     * 发送通知给单个用户
     */
    private void sendToUser(Long userId, String type, String title, String content, String linkUrl) {
        // 检查是否已发送过（幂等）
        String key = "notification:sent:" + userId + ":" + title;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);

        // 标记已发送（24小时过期）
        redisTemplate.opsForValue().set(key, "1", java.time.Duration.ofHours(24));

        // 更新用户未读数缓存
        String unreadKey = "notification:unread:" + userId;
        redisTemplate.opsForValue().increment(unreadKey);
    }
}
