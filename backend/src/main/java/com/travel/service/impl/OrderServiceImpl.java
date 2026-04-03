package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Order;
import com.travel.mapper.OrderMapper;
import com.travel.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public IPage<Order> listOrders(Integer page, Integer pageSize, Long userId, Integer status, Integer orderType) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) wrapper.eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        if (orderType != null) wrapper.eq(Order::getOrderType, orderType);
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order getOrderByNo(String orderNo) {
        return orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
    }

    @Override
    public void createOrder(Order order) {
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setStatus(1);  // 待支付
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);
    }

    @Override
    public void payOrder(String orderNo, String payChannel) {
        Order order = getOrderByNo(orderNo);
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2);
            order.setPayTime(LocalDateTime.now());
            order.setPayChannel(payChannel);
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public void cancelOrder(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order != null && order.getUserId().equals(userId) && order.getStatus() == 1) {
            order.setStatus(3);
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public void refundOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 2) {
            order.setStatus(4);
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }
}
