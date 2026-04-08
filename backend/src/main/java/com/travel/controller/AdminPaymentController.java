package com.travel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.dto.PaymentVO;
import com.travel.entity.Order;
import com.travel.entity.Payment;
import com.travel.entity.User;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.PaymentMapper;
import com.travel.mapper.UserMapper;
import com.travel.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 管理端支付控制器
 */
@RestController
@RequestMapping("/api/v1/admin/payment")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;

    public AdminPaymentController(PaymentService paymentService, UserMapper userMapper, 
                                  OrderMapper orderMapper, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.paymentMapper = paymentMapper;
    }

    /**
     * 获取支付记录列表（管理端）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Page<Payment> pageParam = new Page<>(page, pageSize);
        IPage<PaymentVO> result = paymentService.listPayments(pageParam, status, orderType, keyword, startDate, endDate);
        
        // 计算统计数据
        Map<String, Object> stats = paymentService.getPaymentStats(startDate, endDate);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("stats", stats);
        
        return Result.ok(data);
    }

    /**
     * 获取支付详情
     */
    @GetMapping("/{id}")
    public Result<PaymentVO> detail(@PathVariable Long id) {
        Payment payment = paymentService.getById(id);
        if (payment == null) {
            return Result.error("支付记录不存在");
        }
        
        PaymentVO vo = convertToVO(payment);
        return Result.ok(vo);
    }

    /**
     * 处理退款
     */
    @PostMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        Payment payment = paymentService.getById(id);
        if (payment == null) {
            return Result.error("支付记录不存在");
        }
        
        String reason = params != null ? params.get("reason") : "管理员手动退款";
        paymentService.refundById(id, reason);
        
        return Result.ok();
    }

    /**
     * 导出支付记录为 Excel
     */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) throws IOException {
        
        // 查询所有符合条件的数据（不分页，最多导出10000条）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 日期筛选
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge(Payment::getCreatedAt, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le(Payment::getCreatedAt, endDate + " 23:59:59");
        }
        // 状态筛选
        if (status != null) {
            queryWrapper.eq(Payment::getPayStatus, status);
        }
        
        queryWrapper.orderByDesc(Payment::getCreatedAt);
        queryWrapper.last("LIMIT 10000");
        
        List<Payment> payments = paymentMapper.selectList(queryWrapper);
        
        // 准备导出数据
        List<Map<String, Object>> rows = new ArrayList<>();
        
        for (Payment payment : payments) {
            PaymentVO vo = convertToVO(payment);
            
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("支付单号", vo.getPaymentNo());
            row.put("关联订单", vo.getOrderNo());
            row.put("用户昵称", vo.getUserName() != null ? vo.getUserName() : "-");
            row.put("用户ID", vo.getUserId());
            row.put("订单类型", getOrderTypeName(vo.getOrderType()));
            row.put("消费内容", vo.getTargetName() != null ? vo.getTargetName() : "-");
            row.put("支付金额(元)", vo.getAmount());
            row.put("支付方式", getChannelName(vo.getPayChannel()));
            row.put("支付状态", getStatusName(vo.getPayStatus()));
            row.put("交易流水号", vo.getTransactionId() != null ? vo.getTransactionId() : "-");
            row.put("退款金额(元)", vo.getRefundAmount() != null ? vo.getRefundAmount() : BigDecimal.ZERO);
            row.put("下单时间", vo.getCreatedAt() != null ? formatDateTime(vo.getCreatedAt()) : "-");
            row.put("支付时间", vo.getPayTime() != null ? formatDateTime(vo.getPayTime()) : "-");
            row.put("退款时间", vo.getRefundTime() != null ? formatDateTime(vo.getRefundTime()) : "-");
            
            rows.add(row);
        }
        
        // 生成文件名
        String fileName = "支付记录_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", 
            "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        
        // 使用 Hutool ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter(true);  // true 表示使用 Map 模式
        
        // 写入数据
        writer.write(rows, true);
        
        // 刷新到响应流
        writer.flush(response.getOutputStream());
        writer.close();
    }

    /**
     * 获取导出统计摘要
     */
    @GetMapping("/export/summary")
    public Result<Map<String, Object>> getExportSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> stats = paymentService.getPaymentStats(startDate, endDate);
        return Result.ok(stats);
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
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderNo, payment.getOrderNo())
            );
            if (order != null) {
                vo.setOrderType(order.getOrderType());
                vo.setTargetName(order.getTargetName());
            }
        }
        
        return vo;
    }

    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取订单类型名称
     */
    private String getOrderTypeName(Integer orderType) {
        if (orderType == null) return "-";
        switch (orderType) {
            case 1: return "景点门票";
            case 2: return "酒店预订";
            default: return "其他";
        }
    }

    /**
     * 获取支付渠道名称
     */
    private String getChannelName(String channel) {
        if (channel == null) return "-";
        switch (channel) {
            case "wechat": return "微信支付";
            case "alipay": return "支付宝";
            case "bank": return "银行卡";
            case "balance": return "余额";
            default: return channel;
        }
    }

    /**
     * 获取支付状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "-";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            case 3: return "已退款";
            default: return "未知";
        }
    }
}
