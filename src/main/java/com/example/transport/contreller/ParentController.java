package com.example.transport.contreller;

import com.example.transport.Repositories.GPSLocationRepository;
import com.example.transport.Repositories.TripRepository;
import com.example.transport.dto.ETAResponseDTO;
import com.example.transport.entitie.*;
import com.example.transport.entitie.enumeration.NotificationType;
import com.example.transport.services.ETAService;
import com.example.transport.services.NotificationService;
import com.example.transport.services.PenaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/parent")
@CrossOrigin(origins = "http://localhost:3000")
public class ParentController {

    @Autowired
    private ETAService etaService;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private GPSLocationRepository gpsRepository;

    /* =========================================================
       1️⃣ ETA du bus pour le parent
       ========================================================= */
    @GetMapping("/eta")
    public ResponseEntity<ETAResponseDTO> getMyETA(
            @RequestHeader("X-Parent-Id") Long parentId) {

        ETAResponseDTO eta = etaService.calculateETAForParent(parentId);
        return ResponseEntity.ok(eta);
    }

    /* =========================================================
       2️⃣ Mes pénalités
       ========================================================= */
    @GetMapping("/penalties")
    public ResponseEntity<List<Penalty>> getMyPenalties(
            @RequestHeader("X-Parent-Id") Long parentId) {

        return ResponseEntity.ok(
                penaltyService.getPenaltiesByParent(parentId)
        );
    }

    /* =========================================================
       3️⃣ Confirmer récupération enfant à temps
       ========================================================= */
    @PostMapping("/confirm-pickup/{tripStopId}")
    public ResponseEntity<?> confirmPickup(
            @RequestHeader("X-Parent-Id") Long parentId,
            @PathVariable Long tripStopId) {

        penaltyService.clearPenaltyForOnTimePickup(parentId, tripStopId);

        notificationService.sendNotificationToParent(
                parentId,
                "Ramassage confirmé",
                "Vous avez récupéré votre enfant à temps",
                NotificationType.ARRIVAL
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Ramassage confirmé avec succès"
        ));
    }

    /* =========================================================
       4️⃣ Position temps réel du bus
       ========================================================= */
    @GetMapping("/bus-position")
    public ResponseEntity<?> getMyBusPosition(
            @RequestHeader("X-Parent-Id") Long parentId) {

        Optional<Trip> activeTripOpt =
                tripRepository.findActiveTripByParentId(parentId);

        if (activeTripOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "available", false,
                    "message", "Aucun trajet actif"
            ));
        }

        Trip trip = activeTripOpt.get();
        Vehicle vehicle = trip.getVehicle();

        if (vehicle == null) {
            return ResponseEntity.ok(Map.of(
                    "available", false,
                    "message", "Aucun véhicule assigné"
            ));
        }

        Optional<GPSLocation> lastLocationOpt =
                gpsRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicle.getId());

        if (lastLocationOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "available", false,
                    "message", "Position GPS indisponible"
            ));
        }

        GPSLocation gps = lastLocationOpt.get();
        ETAResponseDTO eta = etaService.calculateETAForParent(parentId);

        Map<String, Object> driverInfo = null;
        if (vehicle.getDriver() != null) {
            Driver driver = vehicle.getDriver();
            driverInfo = Map.of(
                    "id", driver.getId(),
                    "firstName", driver.getFirstName(),
                    "lastName", driver.getLastName()
            );
        }

        return ResponseEntity.ok(Map.of(
                "available", true,
                "tripId", trip.getId(),
                "vehicle", Map.of(
                        "id", vehicle.getId(),
                        "plateNumber", vehicle.getPlateNumber(),
                        "model", vehicle.getModel(),
                        "driver", driverInfo
                ),
                "position", Map.of(
                        "latitude", gps.getLatitude(),
                        "longitude", gps.getLongitude(),
                        "timestamp", gps.getTimestamp()
                ),
                "eta", Map.of(
                        "minutes", eta.getEstimatedMinutes(),
                        "distanceKm", eta.getDistanceKm(),
                        "isClose", eta.isClose(),
                        "message", eta.getMessage()
                )
        ));
    }

    /* =========================================================
       5️⃣ Notifications non lues
       ========================================================= */
    @GetMapping("/notifications/unread")
    public ResponseEntity<?> getUnreadNotifications(
            @RequestHeader("X-Parent-Id") Long parentId) {

        return ResponseEntity.ok(Map.of(
                "count", notificationService.countUnreadNotifications(parentId),
                "notifications",
                notificationService.getUnreadUserNotifications(parentId)
        ));
    }

    /* =========================================================
       6️⃣ Marquer notifications comme lues
       ========================================================= */
    @PostMapping("/notifications/mark-all-read")
    public ResponseEntity<?> markAllRead(
            @RequestHeader("X-Parent-Id") Long parentId) {

        notificationService.markAllAsRead(parentId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Notifications marquées comme lues"
        ));
    }
}
