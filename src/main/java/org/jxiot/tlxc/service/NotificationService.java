package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.Notification;
import org.jxiot.tlxc.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public List<Notification> getUserNotifications(Integer userId, int limit) {
        return notificationMapper.findByUserId(userId, limit);
    }

    public int getUnreadCount(Integer userId) {
        return notificationMapper.countUnread(userId);
    }

    @Transactional
    public void sendNotification(Notification notification) {
        notificationMapper.insert(notification);
    }

    @Transactional
    public void sendBatchNotifications(List<Notification> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            notificationMapper.batchInsert(notifications);
        }
    }

    @Transactional
    public void markAsRead(Integer id) {
        notificationMapper.markAsRead(id);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Transactional
    public void deleteNotification(Integer id) {
        notificationMapper.deleteById(id);
    }
}
