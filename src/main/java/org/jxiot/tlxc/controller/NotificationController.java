package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ApiResponse getNotifications(HttpServletRequest request,
                                         @RequestParam(defaultValue = "50") int limit) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(notificationService.getUserNotifications(userId, limit));
    }

    @GetMapping("/unread-count")
    public ApiResponse getUnreadCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ApiResponse markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    public ApiResponse markAllAsRead(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ApiResponse.success(null);
    }
}
