package com.travel.mq;

import com.travel.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单消息生产者
 * 
 * 用于发送订单相关的异步消息：
 * 1. 订单创建
 * 2. 订单支付成功
 * 3. 订单取消
 * 4. 订单退款
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送订单创建消息
     */
    public void sendOrderCreated(Long orderId, String orderNo) {
        log.info("发送订单创建消息: orderId={}, orderNo={}", orderId, orderNo);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                "order.created",
                new OrderMessage(orderId, orderNo, "CREATED", null)
        );
    }

    /**
     * 发送订单支付成功消息
     */
    public void sendOrderPaid(Long orderId, String orderNo, String payChannel) {
        log.info("发送订单支付成功消息: orderId={}, orderNo={}, channel={}", orderId, orderNo, payChannel);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                "order.paid",
                new OrderMessage(orderId, orderNo, "PAID", payChannel)
        );
    }

    /**
     * 发送订单取消消息
     */
    public void sendOrderCanceled(Long orderId, String orderNo, String reason) {
        log.info("发送订单取消消息: orderId={}, orderNo={}, reason={}", orderId, orderNo, reason);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                "order.canceled",
                new OrderMessage(orderId, orderNo, "CANCELED", reason)
        );
    }

    /**
     * 发送订单退款消息
     */
    public void sendOrderRefunded(Long orderId, String orderNo, String refundReason) {
        log.info("发送订单退款消息: orderId={}, orderNo={}, reason={}", orderId, orderNo, refundReason);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                "order.refunded",
                new OrderMessage(orderId, orderNo, "REFUNDED", refundReason)
        );
    }

    /**
     * 订单消息实体
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class OrderMessage {
        private Long orderId;
        private String orderNo;
        private String status;
        private String extra;  // 支付渠道 / 取消原因 / 退款原因
    }
}
