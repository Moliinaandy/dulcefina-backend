package com.dulcefina.controller;

import com.dulcefina.dto.MarkAsReadRequest;
import com.dulcefina.dto.NotificationDTO;
import com.dulcefina.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<?> markAsRead(@RequestBody MarkAsReadRequest request) {
        try {
            notificationService.markAsRead(request.getIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al marcar notificaciones: " + e.getMessage());
        }
    }
}