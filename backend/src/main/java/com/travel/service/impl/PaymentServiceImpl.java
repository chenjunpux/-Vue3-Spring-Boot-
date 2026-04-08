package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.dto.PaymentVO;
import com.travel.entity.Order;
import com.travel.entity.Payment;
import com.travel.entity.User;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.PaymentMapper;
import com.travel.mapper.UserMapper;
import com.travel.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public PaymentServiceImpl(OrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Map<String, Object> createPayment(String orderNo, String payChannel) {
        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许支付");
        }

        String paymentNo = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Payment payment = new Payment();
        payment.setPaymentNo(paymentNo);
        payment.setOrderNo(orderNo);
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getPayAmount());
        payment.setPayChannel(payChannel);
        payment.setPayStatus(0);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setRefundAmount(BigDecimal.ZERO);
        baseMapper.insert(payment);

        Map<String, Object> result = new HashMap<>();
        result.put("paymentNo", paymentNo);
        result.put("orderNo", orderNo);
        result.put("amount", order.getPayAmount());
        result.put("payChannel", payChannel);

        if ("wechat".equals(payChannel)) {
            result.put("qrCode", "weixin://wxpay/bizpayurl?pr=" + paymentNo);
            result.put("payUrl", "weixin://");
        } else if ("alipay".equals(payChannel)) {
            result.put("qrCode", "https://qr.alipay.com/" + paymentNo);
            result.put("payUrl", "alipay://");
        } else {
            result.put("payUrl", "/api/v1/payment/" + paymentNo + "/callback");
        }
        result.put("expireTime", LocalDateTime.now().plusMinutes(30));
        return result;
    }

    @Override
    @Transactional
    public void handleCallback(String paymentNo, String transactionId, Map<String, String> params) {
        Payment payment = baseMapper.selectOne(
            new LambdaQueryWrapper<Payment>().eq(Payment::getPaymentNo, paymentNo)
        );
        if (payment == null || payment.getPayStatus() != 0) {
            return;
        }

        payment.setPayStatus(1);
        payment.setPayTime(LocalDateTime.now());
        payment.setTransactionId(transactionId);
        payment.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(payment);

        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, payment.getOrderNo())
        );
        if (order != null) {
            order.setStatus(2);
            order.setPayTime(LocalDateTime.now());
            order.setPayChannel(payment.getPayChannel());
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    @Transactional
    public void refund(String paymentNo, String reason) {
        Payment payment = baseMapper.selectOne(
            new LambdaQueryWrapper<Payment>().eq(Payment::getPaymentNo, paymentNo)
        );
        if (payment == null || payment.getPayStatus() != 1) {
            throw new RuntimeException("支付记录不存在或状态不允许退款");
        }

        doRefund(payment);
    }

    @Override
    @Transactional
    public void refundById(Long id, String reason) {
        Payment payment = baseMapper.selectById(id);
        if (payment == null || payment.getPayStatus() != 1) {
            throw new RuntimeException("支付记录不存在或状态不允许退款");
        }

        doRefund(payment);
    }

    /**
     * 执行退款
     */
    private void doRefund(Payment payment) {
        payment.setPayStatus(3); // 3-已退款
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundTime(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(payment);

        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, payment.getOrderNo())
        );
        if (order != null) {
            order.setStatus(4); // 4-已退款
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public Payment getByOrderNo(String orderNo) {
        return baseMapper.selectOne(
            new LambdaQueryWrapper<Payment>().eq(Payment::getOrderNo, orderNo)
        );
    }

    @Override
    public IPage<PaymentVO> listPayments(Page<Payment> page, Integer status, Integer orderType, 
                                         String keyword, String startDate, String endDate) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (status != null && status > 0) {
            wrapper.eq(Payment::getPayStatus, status);
        }
        
        // 日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(Payment::getCreatedAt, LocalDateTime.parse(startDate + " 00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(Payment::getCreatedAt, LocalDateTime.parse(endDate + " 23:59:59"));
        }
        
        // 关键词筛选（用户ID或订单号）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(Payment::getPaymentNo, keyword)
                .or()
                .like(Payment::getOrderNo, keyword)
            );
        }
        
        wrapper.orderByDesc(Payment::getCreatedAt);
        
        // 执行分页查询
        Page<Payment> paymentPage = baseMapper.selectPage(page, wrapper);
        
        // 转换为 VO
        Page<PaymentVO> voPage = new Page<>(paymentPage.getCurrent(), paymentPage.getSize(), paymentPage.getTotal());
        List<PaymentVO> voList = paymentPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Map<String, Object> getPaymentStats(String startDate, String endDate) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(Payment::getCreatedAt, LocalDateTime.parse(startDate + " 00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(Payment::getCreatedAt, LocalDateTime.parse(endDate + " 23:59:59"));
        }
        
        List<Payment> payments = baseMapper.selectList(wrapper);
        
        // 计算统计数据
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;
        int successCount = 0;
        int refundCount = 0;
        
        for (Payment p : payments) {
            if (p.getPayStatus() != null) {
                if (p.getPayStatus() == 1) { // 已支付
                    totalIncome = totalIncome.add(p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO);
                    successCount++;
                } else if (p.getPayStatus() == 3) { // 已退款
                    refundAmount = refundAmount.add(p.getRefundAmount() != null ? p.getRefundAmount() : BigDecimal.ZERO);
                    refundCount++;
                }
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome); // 保持元为单位
        stats.put("refundAmount", refundAmount); // 保持元为单位
        stats.put("successCount", successCount);
        stats.put("refundCount", refundCount);
        
        return stats;
    }

    /**
     * 将 Payment 转换为 PaymentVO
     */
    private PaymentVO convertToVO(Payment payment) {
        PaymentVO vo = new PaymentVO();
        vo.setId(payment.getId());
        vo.setPaymentNo(payment.getPaymentNo());
        vo.setOrderNo(payment.getOrderNo());
        vo.setUserId(payment.getUserId());
        vo.setAmount(payment.getAmount());
        vo.setPayChannel(payment.getPayChannel());
        vo.setPayStatus(payment.getPayStatus());
        vo.setPayTime(payment.getPayTime());
        vo.setTransactionId(payment.getTransactionId());
        vo.setRefundAmount(payment.getRefundAmount());
        vo.setRefundTime(payment.getRefundTime());
        vo.setCreatedAt(payment.getCreatedAt());
        
        // 获取用户信息
        if (payment.getUserId() != null) {
            User user = userMapper.selectById(payment.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
                vo.setUserAvatar(user.getAvatar());
            }
        }
        
        // 获取订单信息
        if (payment.getOrderNo() != null) {
            Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, payment.getOrderNo())
            );
            if (order != null) {
                vo.setOrderType(order.getOrderType());
                vo.setTargetName(order.getTargetName());
            }
        }
        
        return vo;
    }
}
