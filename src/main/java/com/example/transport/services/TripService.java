package com.example.transport.services;

import com.example.transport.Repositories.*;
import com.example.transport.entitie.*;
import com.example.transport.entitie.enumeration.TripStatus;
import com.example.transport.entitie.enumeration.TripStopStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private TripStopRepository tripStopRepository;

    /* ===================== ADMIN ===================== */

    public Trip createTrip(Long vehicleId, List<Long> parentIds) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setStatus(TripStatus.SCHEDULED);
        trip = tripRepository.save(trip);

        int order = 1;
        for (Long parentId : parentIds) {
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent introuvable"));

            TripStop stop = new TripStop();
            stop.setTrip(trip);
            stop.setParent(parent);
            stop.setStopOrder(order++);
            stop.setStatus(TripStopStatus.SCHEDULED);
            // On peut définir une heure prévue ici si nécessaire
            stop.setScheduledArrival(LocalDateTime.now().plusMinutes(order * 10));

            tripStopRepository.save(stop);
        }
        return trip;
    }

    /* ===================== GESTION DU TRAJET ===================== */

    public Trip startTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartTime(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    public Trip endTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    /** * Cette méthode est désormais intégrée ou appelée par le TrackingService 
     * pour assurer la cohérence du statut "ARRIVED"
     */
    public void processVehicleLocation(Long vehicleId, double lat, double lng) {
        // CORRECTION : Utilisation de la méthode existante dans le Repository réécrit
        Optional<TripStop> nextStopOpt = tripStopRepository
                .findFirstByTrip_Vehicle_IdAndStatusOrderByScheduledArrivalAsc(
                        vehicleId,
                        TripStopStatus.WAITING
                );

        if (nextStopOpt.isPresent()) {
            TripStop nextStop = nextStopOpt.get();
            Parent p = nextStop.getParent();

            // Calcul de distance simple (ou via GeoUtils si vous préférez)
            double distance = calculateDistance(lat, lng, p.getLatitude(), p.getLongitude());

            if (distance <= 0.05) { // 50 mètres
                // Logique d'arrivée automatique
                nextStop.setStatus(TripStopStatus.WAITING); // Ou un statut spécifique ARRIVED
                nextStop.setActualArrival(LocalDateTime.now());
                tripStopRepository.save(nextStop);
                // Envoyer notification ici si nécessaire
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<Trip> getActiveTripByParentId(Long parentId) {
        return tripRepository.findActiveTripByParentId(parentId);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
