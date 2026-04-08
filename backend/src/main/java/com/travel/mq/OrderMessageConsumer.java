package com.travel.mq;

import com.rabbitmq.client.Channel;
import com.travel.config.RabbitMQConfig;
import com.travel.entity.Notification;
import com.travel.mapper.NotificationMapper;
import com.travel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 订单消息消费者
 * 
 * 消费订单相关的异步消息：
 * 1. 订单创建 - 更新搜索索引
 * 2. 订单支付 - 发送通知、更新库存、发送邮件
 * 3. 订单取消 - 恢复库存、发送通知
 * 4. 订单退款 - 更新统计、发送通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final NotificationMapper notificationMapper;
    private final NotificationMessageProducer notificationProducer;
    private final SearchService searchService;

    /**
     * 订单创建消息处理
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATE_QUEUE)
    public void handleOrderCreated(OrderMessageProducer.OrderMessage message, 
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("消费订单创建消息: orderNo={}", message.getOrderNo());
            
            // 1. 更新搜索索引（增加销量）
            // searchService.incrementSales(message.getOrderId());
            
            // 2. 记录日志
            log.info("订单创建消息处理成功: orderNo={}", message.getOrderNo());
            
            // 确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理订单创建消息失败: orderNo={}", message.getOrderNo(), e);
            // 拒绝消息，重试
            channel.basicNack(tag, false, true);
        }
    }

    /**
     * 订单支付成功消息处理
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_NOTIFY_QUEUE)
    public void handleOrderPaid(OrderMessageProducer.OrderMessage message,
                                 Channel channel,
                                 @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("消费订单支付成功消息: orderNo={}", message.getOrderNo());
            
            // 1. 发送站内通知
            sendNotification(message.getOrderId(), "order_pay", 
                    "支付成功", 
                    "您的订单 " + message.getOrderNo() + " 已支付成功");
            
            // 2. 更新搜索索引（增加销量）
            // searchService.incrementSales(message.getOrderId());
            
            // 3. 如果是高价值订单，发送邮件通知（可选）
            // if (order.getPayAmount().compareTo(new BigDecimal("1000")) > 0) {
            //     emailProducer.sendOrderPaidEmail(message);
            // }
            
            log.info("订单支付成功消息处理成功: orderNo={}", message.getOrderNo());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理订单支付成功消息失败: orderNo={}", message.getOrderNo(), e);
            channel.basicNack(tag, false, true);
        }
    }

    /**
     * 订单取消消息处理
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CANCEL_QUEUE)
    public void handleOrderCanceled(OrderMessageProducer.OrderMessage message,
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("消费订单取消消息: orderNo={}", message.getOrderNo());
            
            // 1. 恢复库存（景点门票/酒店房间）
            // inventoryService.releaseInventory(message.getOrderId());
            
            // 2. 发送站内通知
            sendNotification(message.getOrderId(), "order_cancel",
                    "订单已取消",
                    "您的订单 " + message.getOrderNo() + " 已取消");
            
            log.info("订单取消消息处理成功: orderNo={}", message.getOrderNo());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理订单取消消息失败: orderNo={}", message.getOrderNo(), e);
            channel.basicNack(tag, false, true);
        }
    }

    /**
     * 订单退款消息处理
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_REFUND_QUEUE)
    public void handleOrderRefunded(OrderMessageProducer.OrderMessage message,
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("消费订单退款消息: orderNo={}", message.getOrderNo());
            
            // 1. 恢复库存
            // inventoryService.releaseInventory(message.getOrderId());
            
            // 2. 发送站内通知
            sendNotification(message.getOrderId(), "refund",
                    "退款成功",
                    "您的订单 " + message.getOrderNo() + " 已退款成功");
            
            // 3. 更新统计数据
            // statisticsService.updateRefundStat(message.getOrderId());
            
            log.info("订单退款消息处理成功: orderNo={}", message.getOrderNo());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理订单退款消息失败: orderNo={}", message.getOrderNo(), e);
            channel.basicNack(tag, false, true);
        }
    }

    /**
     * 发送站内通知
     */
    private void sendNotification(Long userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
        log.info("发送站内通知成功: userId={}, type={}", userId, type);
    }
}
