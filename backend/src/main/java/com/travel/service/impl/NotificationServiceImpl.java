package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Notification;
import com.travel.entity.SystemNotification;
import com.travel.mapper.NotificationMapper;
import com.travel.mapper.SystemNotificationMapper;
import com.travel.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final SystemNotificationMapper systemNotificationMapper;

    public NotificationServiceImpl(SystemNotificationMapper systemNotificationMapper) {
        this.systemNotificationMapper = systemNotificationMapper;
    }

    @Override
    public List<Notification> getMyNotifications(Long userId, Integer page, Integer pageSize) {
        Page<Notification> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId).orderByDesc(Notification::getCreatedAt);
        Page<Notification> result = baseMapper.selectPage(p, wrapper);
        return result.getRecords();
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return Math.toIntExact(baseMapper.selectCount(
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
        ));
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = baseMapper.selectOne(
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getId, id)
                .eq(Notification::getUserId, userId)
        );
        if (notification != null) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            baseMapper.updateById(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Notification notification = new Notification();
        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        baseMapper.update(notification,
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
        );
    }

    @Override
    @Transactional
    public void sendNotification(Long userId, String type, String title, String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        baseMapper.insert(notification);
    }

    @Override
    @Transactional
    public void sendBulkNotifications(List<Long> userIds, String type, String title, String content) {
        for (Long userId : userIds) {
            sendNotification(userId, type, title, content, null);
        }
    }

    public List<SystemNotification> getPublishedSystemNotifications() {
        LocalDateTime now = LocalDateTime.now();
        return systemNotificationMapper.selectList(
            new LambdaQueryWrapper<SystemNotification>()
                .eq(SystemNotification::getIsPublished, 1)
                .le(SystemNotification::getStartTime, now)
                .ge(SystemNotification::getEndTime, now)
                .orderByDesc(SystemNotification::getPublishedAt)
        );
    }
}
