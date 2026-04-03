package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.entity.Order;

public interface OrderService {
    IPage<Order> listOrders(Integer page, Integer pageSize, Long userId, Integer status, Integer orderType);
    Order getOrderById(Long id);
    Order getOrderByNo(String orderNo);
    void createOrder(Order order);
    void payOrder(String orderNo, String payChannel);
    void cancelOrder(Long id, Long userId);
    void refundOrder(Long id);
}
