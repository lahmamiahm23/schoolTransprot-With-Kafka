package com.example.transport.services;

import com.example.transport.Repositories.*;
import com.example.transport.dto.LocationDTO;
import com.example.transport.entitie.*;
import com.example.transport.entitie.enumeration.TripStopStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TrackingService {

    @Autowired private GPSLocationRepository gpsRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private TripStopRepository tripStopRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processNewLocation(LocationDTO loc) {
        Vehicle vehicle = vehicleRepository.findById(loc.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        // 1. Sauvegarde historique
        GPSLocation history = new GPSLocation();
        history.setLatitude(loc.getLatitude());
        history.setLongitude(loc.getLongitude());
        history.setVehicle(vehicle);
        history.setTimestamp(LocalDateTime.now());
        gpsRepository.save(history);

        // 2. Geofencing : Détection automatique d'approche
        Optional<TripStop> nextStop = tripStopRepository
                .findFirstByTrip_Vehicle_IdAndStatusOrderByScheduledArrivalAsc(
                        vehicle.getId(), TripStopStatus.WAITING);

        if (nextStop.isPresent()) {
            Parent parent = nextStop.get().getParent();
            if (parent.getLatitude() != null && parent.getLongitude() != null) {
                double distance = calculateDistance(
                        loc.getLatitude(), loc.getLongitude(),
                        parent.getLatitude(), parent.getLongitude()
                );

                // Seuil de 50 mètres (0.05 km)
                if (distance <= 0.05) {
                    loc.setStatus("ARRIVED");
                    loc.setSpeed(0.0); // Immobilisation visuelle pour le parent
                } else {
                    loc.setStatus("EN_ROUTE");
                }
            }
        }

        // 3. Envoi au WebSocket pour mise à jour réelle sur la map du père
        messagingTemplate.convertAndSend("/topic/vehicles", loc);
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
