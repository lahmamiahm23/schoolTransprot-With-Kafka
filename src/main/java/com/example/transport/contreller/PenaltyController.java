package com.example.transport.contreller;

import com.example.transport.entitie.Penalty;
import com.example.transport.services.PenaltyService;
import com.example.transport.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@CrossOrigin(origins = "http://localhost:3000")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Récupérer les pénalités d'un parent
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Penalty>> getParentPenalties(@PathVariable Long parentId) {
        try {
            List<Penalty> penalties = penaltyService.getPenaltiesByParent(parentId);
            return ResponseEntity.ok(penalties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Simuler une pénalité pour retard > 5 minutes
     */
    @PostMapping("/simulate-late-pickup/{parentId}")
    public ResponseEntity<String> simulateLatePickup(@PathVariable Long parentId) {
        try {
            // Créer une pénalité pour retard
            penaltyService.issuePenalty(parentId,
                    "Retard au ramassage (> 5 minutes)",
                    10.0); // Montant de la pénalité

            // Envoyer notification au parent
            notificationService.sendPenaltyNotification(parentId,
                    "Retard au ramassage",
                    10.0);

            return ResponseEntity.ok("Pénalité appliquée au parent " + parentId + " pour retard");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Marquer une pénalité comme payée
     */
    @PutMapping("/pay/{penaltyId}")
    public ResponseEntity<String> payPenalty(@PathVariable Long penaltyId) {
        try {
            penaltyService.markAsPaid(penaltyId);
            return ResponseEntity.ok("Pénalité " + penaltyId + " marquée comme payée");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Confirmer que le parent a récupéré l'enfant à temps (annule pénalité)
     */
    @PostMapping("/confirm-ontime-pickup/{parentId}/{tripStopId}")
    public ResponseEntity<String> confirmOnTimePickup(
            @PathVariable Long parentId,
            @PathVariable Long tripStopId) {
        try {
            penaltyService.clearPenaltyForOnTimePickup(parentId, tripStopId);
            notificationService.sendNotificationToParent(parentId,
                    "Ramassage confirmé",
                    "Vous avez récupéré votre enfant à temps",
                    com.example.transport.entitie.enumeration.NotificationType.ARRIVAL);

            return ResponseEntity.ok("Ramassage confirmé - pas de pénalité");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
