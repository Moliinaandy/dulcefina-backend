package com.dulcefina.service;

import com.dulcefina.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getUnreadNotifications();
    void markAsRead(List<String> notificationIds);
}