package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.dto.PaymentVO;
import com.travel.entity.Payment;

import java.util.Map;

public interface PaymentService extends IService<Payment> {
    
    /**
     * 创建支付订单
     */
    Map<String, Object> createPayment(String orderNo, String payChannel);
    
    /**
     * 处理支付回调
     */
    void handleCallback(String paymentNo, String transactionId, Map<String, String> params);
    
    /**
     * 退款（通过支付单号）
     */
    void refund(String paymentNo, String reason);
    
    /**
     * 退款（通过ID）
     */
    void refundById(Long id, String reason);
    
    /**
     * 根据订单号获取支付记录
     */
    Payment getByOrderNo(String orderNo);
    
    /**
     * 管理端：分页获取支付记录列表
     */
    IPage<PaymentVO> listPayments(Page<Payment> page, Integer status, Integer orderType, String keyword, String startDate, String endDate);
    
    /**
     * 管理端：获取支付统计数据
     */
    Map<String, Object> getPaymentStats(String startDate, String endDate);
}
