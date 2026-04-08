package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.SystemNotification;
import com.travel.entity.User;
import com.travel.mapper.SystemNotificationMapper;
import com.travel.mq.NotificationMessageProducer;
import com.travel.service.NotificationService;
import com.travel.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员通知管理控制器
 */
@RestController
@RequestMapping("/api/v1/admin/notification")
public class AdminNotificationController {

    private final SystemNotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final NotificationMessageProducer messageProducer;

    public AdminNotificationController(
            SystemNotificationMapper notificationMapper,
            NotificationService notificationService,
            NotificationMessageProducer messageProducer) {
        this.notificationMapper = notificationMapper;
        this.notificationService = notificationService;
        this.messageProducer = messageProducer;
    }

    /**
     * 分页查询通知列表
     */
    @GetMapping("/list")
    public Result<Page<SystemNotification>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {

        Page<SystemNotification> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<SystemNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, SystemNotification::getType, type)
               .eq(status != null, SystemNotification::getStatus, status)
               .orderByDesc(SystemNotification::getCreatedAt);

        Page<SystemNotification> result = notificationMapper.selectPage(p, wrapper);
        return Result.ok(result);
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public Result<SystemNotification> detail(@PathVariable Long id) {
        SystemNotification n = notificationMapper.selectById(id);
        if (n == null) return Result.error("通知不存在");
        return Result.ok(n);
    }

    /**
     * 创建 / 更新通知
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody SystemNotification notification) {
        if (notification.getTitle() == null || notification.getTitle().isBlank()) {
            return Result.error("标题不能为空");
        }
        if (notification.getContent() == null || notification.getContent().isBlank()) {
            return Result.error("内容不能为空");
        }
        if (notification.getType() == null) notification.setType(1);
        if (notification.getTargetType() == null) notification.setTargetType(1);
        if (notification.getStatus() == null) notification.setStatus(0);

        if (notification.getId() != null) {
            // 更新
            notificationMapper.updateById(notification);
            return Result.ok();
        }

        // 新增
        notification.setStatus(0);
        notificationMapper.insert(notification);
        return Result.ok();
    }

    /**
     * 发布通知（发送给用户）
     */
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        SystemNotification n = notificationMapper.selectById(id);
        if (n == null) return Result.error("通知不存在");

        n.setStatus(1);
        n.setPublishedAt(LocalDateTime.now());
        notificationMapper.updateById(n);

        // 发送消息队列，异步推送给用户
        try {
            if (n.getTargetType() == 1) {
                // 全部用户
                messageProducer.sendNotificationAll(n.getId(), n.getTitle(), n.getContent(), n.getType());
            } else if (n.getTargetType() == 2 && n.getTargetUserIds() != null) {
                // 指定用户
                messageProducer.sendNotificationBatch(n.getId(), n.getTitle(), n.getContent(), n.getType(), n.getTargetUserIds());
            }
        } catch (Exception e) {
            // 发布成功，MQ 异步发送，不影响主流程
        }

        return Result.ok();
    }

    /**
     * 保存草稿
     */
    @PostMapping("/{id}/draft")
    public Result<Void> saveDraft(@RequestBody SystemNotification notification) {
        notification.setStatus(0);
        if (notification.getId() != null) {
            notificationMapper.updateById(notification);
        } else {
            notification.setStatus(0);
            notificationMapper.insert(notification);
        }
        return Result.ok();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        notificationMapper.deleteById(id);
        return Result.ok();
    }
}
