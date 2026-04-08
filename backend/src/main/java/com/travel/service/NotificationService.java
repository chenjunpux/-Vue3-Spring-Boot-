package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Notification;
import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<Notification> getMyNotifications(Long userId, Integer page, Integer pageSize);
    Integer getUnreadCount(Long userId);
    void markAsRead(Long id, Long userId);
    void markAllAsRead(Long userId);
    void sendNotification(Long userId, String type, String title, String content, Long relatedId);
    void sendBulkNotifications(List<Long> userIds, String type, String title, String content);
}
