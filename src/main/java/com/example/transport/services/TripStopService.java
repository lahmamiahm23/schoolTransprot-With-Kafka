package com.example.transport.services;

import com.example.transport.Repositories.TripStopRepository;
import com.example.transport.entitie.Penalty;
import com.example.transport.entitie.TripStop;
import com.example.transport.entitie.User;
import com.example.transport.entitie.enumeration.PenaltyStatus;
import com.example.transport.entitie.enumeration.TripStopStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TripStopService {

    @Autowired
    private TripStopRepository tripStopRepository;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private NotificationService notificationService;



    public TripStop arriveAtStop(TripStop stop) {
        stop.setActualArrival(LocalDateTime.now());
        stop.setStatus(TripStopStatus.WAITING);
        return tripStopRepository.save(stop);
    }
    /**
     * Marquer un arrêt comme embarqué ou appliquer pénalité si retard > 5 min
     */
    public TripStop markChildBoarded(Long stopId) {
        TripStop tripStop = tripStopRepository.findById(stopId)
                .orElseThrow(() -> new RuntimeException("Arrêt non trouvé"));

        LocalDateTime now = LocalDateTime.now();
        long minutesWaited = ChronoUnit.MINUTES.between(tripStop.getActualArrival(), now);

        if (minutesWaited > 5) {
            // Appliquer pénalité automatique
            penaltyService.issuePenalty(tripStop.getParent().getId(),
                    "Retard d'embarquement > 5 min", 5.0);

            tripStop.setStatus(TripStopStatus.LATE_BOARDING);
            tripStop.setNotes("Embarcation tardive - pénalité appliquée");
        } else {
            tripStop.setStatus(TripStopStatus.COMPLETED);
            tripStop.setChildPickupTime(now);
            tripStop.setNotes("Embarcation réussie dans les temps");
        }

        tripStopRepository.save(tripStop);

        // Notification au parent
        User parentUser = tripStop.getParent();
        notificationService.sendNotification(
                parentUser,
                "Enfant embarqué",
                "Votre enfant a été embarqué" + (minutesWaited > 5 ? " avec retard." : "."),
                null
        );

        return tripStop;
    }
}
