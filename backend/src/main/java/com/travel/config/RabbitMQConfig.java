package com.travel.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * 
 * 交换机和队列设计：
 * 
 * 交换机（Exchange）               类型     队列（Queue）
 * ────────────────────────────────────────────────────────
 * order.exchange (topic)         topic
 *   ├── order.created       →  order.create.queue
 *   ├── order.paid          →  order.notify.queue
 *   │                              order.notification.queue
 *   ├── order.canceled      →  order.cancel.queue
 *   │                              order.notification.queue
 *   └── order.refunded      →  order.refund.queue
 * 
 * notification.exchange (direct)  direct
 *   └── notification.create  →  notification.queue
 * 
 * payment.exchange (direct)        direct
 *   ├── payment.success     →  payment.success.queue
 *   └── payment.refund      →  payment.refund.queue
 * 
 * search.exchange (fanout)       fanout
 *   └── (广播)                →  search.index.queue
 * 
 * email.exchange (direct)         direct
 *   └── email.send          →  email.queue
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 交换机 ====================
    
    // 订单交换机
    public static final String ORDER_EXCHANGE = "order.exchange";
    
    // 通知交换机
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    
    // 支付交换机
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    
    // 搜索交换机（广播）
    public static final String SEARCH_EXCHANGE = "search.exchange";
    
    // 邮件交换机
    public static final String EMAIL_EXCHANGE = "email.exchange";

    // ==================== 队列 ====================
    
    // 订单相关队列
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";
    public static final String ORDER_NOTIFY_QUEUE = "order.notify.queue";
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    public static final String ORDER_REFUND_QUEUE = "order.refund.queue";
    
    // 通知队列
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    
    // 支付队列
    public static final String PAYMENT_SUCCESS_QUEUE = "payment.success.queue";
    public static final String PAYMENT_REFUND_QUEUE = "payment.refund.queue";
    
    // 搜索队列
    public static final String SEARCH_INDEX_QUEUE = "search.index.queue";
    
    // 邮件队列
    public static final String EMAIL_QUEUE = "email.queue";

    // ==================== 交换机 Bean ====================
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }
    
    @Bean
    public FanoutExchange searchExchange() {
        return new FanoutExchange(SEARCH_EXCHANGE);
    }
    
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EXCHANGE);
    }

    // ==================== 队列 Bean ====================
    
    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE)
                .deadLetterExchange("")  // 死信交换机
                .deadLetterRoutingKey(ORDER_CREATE_QUEUE + ".dlq")
                .build();
    }
    
    @Bean
    public Queue orderNotifyQueue() {
        return QueueBuilder.durable(ORDER_NOTIFY_QUEUE).build();
    }
    
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(ORDER_CANCEL_QUEUE).build();
    }
    
    @Bean
    public Queue orderRefundQueue() {
        return QueueBuilder.durable(ORDER_REFUND_QUEUE).build();
    }
    
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE).build();
    }
    
    @Bean
    public Queue paymentRefundQueue() {
        return QueueBuilder.durable(PAYMENT_REFUND_QUEUE).build();
    }
    
    @Bean
    public Queue searchIndexQueue() {
        return QueueBuilder.durable(SEARCH_INDEX_QUEUE).build();
    }
    
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    // ==================== 绑定 Bean ====================
    
    // 订单交换机绑定
    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder.bind(orderCreateQueue())
                .to(orderExchange())
                .with("order.created");
    }
    
    @Bean
    public Binding orderNotifyBinding() {
        return BindingBuilder.bind(orderNotifyQueue())
                .to(orderExchange())
                .with("order.paid");
    }
    
    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(orderExchange())
                .with("order.canceled");
    }
    
    @Bean
    public Binding orderRefundBinding() {
        return BindingBuilder.bind(orderRefundQueue())
                .to(orderExchange())
                .with("order.refunded");
    }
    
    // 通知交换机绑定
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with("notification.create");
    }
    
    // 支付交换机绑定
    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(paymentSuccessQueue())
                .to(paymentExchange())
                .with("payment.success");
    }
    
    @Bean
    public Binding paymentRefundBinding() {
        return BindingBuilder.bind(paymentRefundQueue())
                .to(paymentExchange())
                .with("payment.refund");
    }
    
    // 搜索交换机绑定（广播，自动绑定所有队列）
    @Bean
    public Binding searchIndexBinding() {
        return BindingBuilder.bind(searchIndexQueue())
                .to(searchExchange());
    }
    
    // 邮件交换机绑定
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(emailExchange())
                .with("email.send");
    }

    // ==================== MessageConverter ====================
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
