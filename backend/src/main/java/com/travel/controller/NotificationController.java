package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.Notification;
import com.travel.entity.User;
import com.travel.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取我的通知列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> myNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer isRead) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        List<Notification> notifications = notificationService.getMyNotifications(user.getId(), page, pageSize);
        Integer unreadCount = notificationService.getUnreadCount(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("list", notifications);
        result.put("unreadCount", unreadCount);
        result.put("total", notifications.size());
        return Result.ok(result);
    }

    /**
     * 获取未读数量
     */
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        return Result.ok(notificationService.getUnreadCount(user.getId()));
    }

    /**
     * 标记单条为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        notificationService.markAsRead(id, user.getId());
        return Result.ok();
    }

    /**
     * 标记全部为已读
     */
    @PutMapping("/read/all")
    public Result<Void> markAllAsRead(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        notificationService.markAllAsRead(user.getId());
        return Result.ok();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        notificationService.removeById(id);
        return Result.ok();
    }
}
