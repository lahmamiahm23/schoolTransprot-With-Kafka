package com.example.transport.contreller;

import com.example.transport.entitie.Notification;
import com.example.transport.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Récupérer les notifications non lues
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Compter les notifications non lues
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        try {
            long count = notificationService.countUnreadNotifications(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0L);
        }
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marquée comme lue");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("Toutes les notifications marquées comme lues");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok("Notification supprimée");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Simuler l'envoi d'une alerte 5 minutes
     */
    @PostMapping("/simulate-alarm/{parentId}/{tripId}")
    public ResponseEntity<String> simulateFiveMinuteAlarm(
            @PathVariable Long parentId,
            @PathVariable Long tripId) {
        try {
            notificationService.sendFiveMinuteAlarm(tripId, parentId);
            return ResponseEntity.ok("Alarme 5 minutes envoyée au parent " + parentId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Simuler une notification d'arrivée
     */
    @PostMapping("/simulate-arrival/{parentId}")
    public ResponseEntity<String> simulateArrivalNotification(@PathVariable Long parentId) {
        try {
            notificationService.sendArrivalNotification(parentId);
            return ResponseEntity.ok("Notification d'arrivée envoyée au parent " + parentId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
