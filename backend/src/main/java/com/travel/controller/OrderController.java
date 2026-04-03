package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.entity.User;
import com.travel.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 我的订单列表
    @GetMapping("/list")
    public Result<IPage<Order>> myOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return Result.ok(orderService.listOrders(page, pageSize, user.getId(), status, null));
        }
        return Result.error(401, "未登录");
    }

    // 管理端订单列表
    @GetMapping("/admin/list")
    public Result<IPage<Order>> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType) {
        return Result.ok(orderService.listOrders(page, pageSize, null, status, orderType));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderById(id));
    }

    // 创建景点订单
    @PostMapping("/create/spot")
    public Result<Order> createSpotOrder(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            Order order = new Order();
            order.setUserId(user.getId());
            order.setOrderType(1);
            order.setTargetId(Long.valueOf(body.get("spotId").toString()));
            order.setTargetName(body.get("spotName").toString());
            order.setTotalAmount(new BigDecimal(body.get("totalAmount").toString()));
            order.setPayAmount(order.getTotalAmount());
            order.setQuantity(Integer.valueOf(body.get("quantity").toString()));
            if (body.get("visitDate") != null) {
                order.setVisitDate(LocalDate.parse(body.get("visitDate").toString()));
            }
            order.setContactName(body.get("contactName") != null ? body.get("contactName").toString() : user.getNickname());
            order.setContactPhone(body.get("contactPhone") != null ? body.get("contactPhone").toString() : user.getPhone());
            orderService.createOrder(order);
            return Result.ok(order);
        }
        return Result.error(401, "未登录");
    }

    // 创建酒店订单
    @PostMapping("/create/hotel")
    public Result<Order> createHotelOrder(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            Order order = new Order();
            order.setUserId(user.getId());
            order.setOrderType(2);
            order.setTargetId(Long.valueOf(body.get("hotelId").toString()));
            order.setTargetName(body.get("hotelName").toString());
            order.setTotalAmount(new BigDecimal(body.get("totalAmount").toString()));
            order.setPayAmount(order.getTotalAmount());
            order.setContactName(body.get("contactName") != null ? body.get("contactName").toString() : user.getNickname());
            order.setContactPhone(body.get("contactPhone") != null ? body.get("contactPhone").toString() : user.getPhone());
            orderService.createOrder(order);
            return Result.ok(order);
        }
        return Result.error(401, "未登录");
    }

    // 模拟支付
    @PostMapping("/{orderNo}/pay")
    public Result<Void> pay(@PathVariable String orderNo,
                            @RequestParam(defaultValue = "wechat") String payChannel) {
        orderService.payOrder(orderNo, payChannel);
        return Result.ok();
    }

    // 取消订单
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(Authentication authentication, @PathVariable Long id) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            orderService.cancelOrder(id, user.getId());
            return Result.ok();
        }
        return Result.error(401, "未登录");
    }

    // 退款
    @PutMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) {
        orderService.refundOrder(id);
        return Result.ok();
    }
}
