package com.example.transport.Repositories;

import com.example.transport.entitie.Trip;
import com.example.transport.entitie.TripStop;
import com.example.transport.entitie.enumeration.TripStopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TripStopRepository extends JpaRepository<TripStop, Long> {

    // CORRECTION : Recherche du premier arrêt en attente pour un véhicule donné
    // Utilisation de l'underscore (_) pour naviguer dans les relations : Trip -> Vehicle -> Id
    Optional<TripStop> findFirstByTrip_Vehicle_IdAndStatusOrderByScheduledArrivalAsc(
            Long vehicleId,
            TripStopStatus status
    );

    List<TripStop> findByTripOrderByScheduledArrivalAsc(Trip trip);

    List<TripStop> findByParentIdAndStatusIn(Long parentId, List<TripStopStatus> statuses);

    List<TripStop> findByTripIdOrderByScheduledArrivalAsc(Long tripId);

    List<TripStop> findByTripIdAndStatus(Long tripId, TripStopStatus status);
}
