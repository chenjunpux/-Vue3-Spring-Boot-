package com.travel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录 VO（用于管理端展示）
 */
@Data
public class PaymentVO {
    
    private Long id;
    
    /**
     * 支付单号
     */
    private String paymentNo;
    
    /**
     * 关联订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户昵称
     */
    private String userName;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 订单类型：1-景点门票，2-酒店预订
     */
    private Integer orderType;
    
    /**
     * 消费内容（景点/酒店名称）
     */
    private String targetName;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 支付渠道：wechat-微信，alipay-支付宝，bank-银行卡，balance-余额
     */
    private String payChannel;
    
    /**
     * 支付状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer payStatus;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 第三方交易流水号
     */
    private String transactionId;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
