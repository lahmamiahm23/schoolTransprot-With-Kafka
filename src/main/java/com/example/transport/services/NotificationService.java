package com.example.transport.services;

import com.example.transport.Repositories.NotificationRepository;
import com.example.transport.Repositories.UserRepository;
import com.example.transport.entitie.Notification;
import com.example.transport.entitie.User;
import com.example.transport.entitie.enumeration.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Créer et sauvegarder une notification
     */
    private Notification createNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setUser(user);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    /**
     * Envoyer une notification à un utilisateur
     */
    public void sendNotification(User user, String message, NotificationType type) {
        // Sauvegarder en base
        Notification notification = createNotification(user, message, type);

        // Envoyer via WebSocket en temps réel
        sendWebSocketNotification(notification, user.getId());
    }

    /**
     * Envoyer une notification avec titre personnalisé
     */
    public void sendNotification(User user, String title, String message, NotificationType type) {
        String fullMessage = (title != null ? title + ": " : "") + message;
        sendNotification(user, fullMessage, type);
    }

    /**
     * Envoyer une notification à un parent par son ID
     */
    public void sendNotificationToParent(Long parentId, String message, NotificationType type) {
        User user = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + parentId));
        sendNotification(user, message, type);
    }

    /**
     * Envoyer une notification avec titre à un parent
     */
    public void sendNotificationToParent(Long parentId, String title, String message, NotificationType type) {
        User user = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + parentId));
        sendNotification(user, title, message, type);
    }

    /**
     * Alarme 5 minutes
     */
    public void sendFiveMinuteAlarm(Long tripId, Long parentId) {
        String message = "ALERTE : Le bus arrive dans 5 minutes !";
        sendNotificationToParent(parentId, "Alarme 5 minutes", message, NotificationType.ETA_ALARM);

        // Envoyer aussi sur le canal du trajet
        messagingTemplate.convertAndSend("/topic/trip/" + tripId, message);
    }

    /**
     * Notification d'arrivée du bus
     */
    public void sendArrivalNotification(Long parentId) {
        String message = "Le bus scolaire est arrivé devant chez vous.";
        sendNotificationToParent(parentId, "Bus arrivé", message, NotificationType.ARRIVAL);
    }

    /**
     * Notification d'arrivée avec message personnalisé
     */
    public void sendArrivalNotification(Long parentId, String customMessage) {
        String message = customMessage != null ? customMessage : "Le bus scolaire est arrivé devant chez vous.";
        sendNotificationToParent(parentId, "Bus arrivé", message, NotificationType.ARRIVAL);
    }

    /**
     * Notification de retard
     */
    public void sendDelayNotification(Long parentId, int delayMinutes) {
        String message = String.format("Le bus a %d minutes de retard.", delayMinutes);
        sendNotificationToParent(parentId, "Retard du bus", message, NotificationType.DELAY);
    }

    /**
     * Notification de pénalité
     */
    public void sendPenaltyNotification(Long parentId, String reason, double amount) {
        String message = String.format("Une pénalité de %.2f DT a été appliquée. Raison: %s", amount, reason);
        sendNotificationToParent(parentId, "Nouvelle pénalité", message, NotificationType.PENALTY);
    }

    /**
     * Envoyer une notification personnalisée
     */
    public void sendCustomNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
        sendNotification(user, title, message, type);
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    public List<Notification> getUnreadUserNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderBySentAtDesc(userId);
    }

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadUserNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Envoyer via WebSocket
     */
    private void sendWebSocketNotification(Notification notification, Long userId) {
        try {
            // Créer un DTO simplifié pour le WebSocket
            NotificationMessageDTO dto = new NotificationMessageDTO(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getType().name(),
                    notification.getSentAt(),
                    notification.isRead()
            );

            // Envoyer à l'utilisateur spécifique
            messagingTemplate.convertAndSend("/topic/user/" + userId, dto);

        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer l'opération principale
            System.err.println("Erreur WebSocket: " + e.getMessage());
        }
    }

    /**
     * DTO pour les messages WebSocket
     */
    public static class NotificationMessageDTO {
        private Long id;
        private String message;
        private String type;
        private LocalDateTime sentAt;
        private boolean read;

        public NotificationMessageDTO(Long id, String message, String type, LocalDateTime sentAt, boolean read) {
            this.id = id;
            this.message = message;
            this.type = type;
            this.sentAt = sentAt;
            this.read = read;
        }

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public LocalDateTime getSentAt() { return sentAt; }
        public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}
