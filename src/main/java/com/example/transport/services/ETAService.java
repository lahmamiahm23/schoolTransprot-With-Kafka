package com.example.transport.services;

import com.example.transport.Repositories.GPSLocationRepository;
import com.example.transport.Repositories.TripRepository;
import com.example.transport.Repositories.StudentRepository;
import com.example.transport.dto.ETAResponseDTO;
import com.example.transport.entitie.GPSLocation;
import com.example.transport.entitie.Trip;
import com.example.transport.entitie.Student;
import com.example.transport.entitie.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

@Service
public class ETAService {

    private static final Logger logger = LoggerFactory.getLogger(ETAService.class);
    private static final double AVERAGE_SPEED_KMH = 20.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private GPSLocationRepository gpsRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Calcul de l'ETA à la demande (Pull) pour l'App Parent
     */
    public ETAResponseDTO calculateETAForParent(Long parentId) {
        try {
            // 1. Trouver le trajet actif pour ce parent
            Optional<Trip> activeTripOpt = tripRepository.findActiveTripByParentId(parentId);

            if (activeTripOpt.isEmpty()) {
                logger.warn("Aucun trajet actif trouvé pour le parent ID: {}", parentId);
                return new ETAResponseDTO(null, 0, false, 0.0, "Aucun trajet actif");
            }

            Trip activeTrip = activeTripOpt.get();
            Vehicle vehicle = activeTrip.getVehicle();

            if (vehicle == null) {
                logger.error("Véhicule non trouvé pour le trajet ID: {}", activeTrip.getId());
                return new ETAResponseDTO(activeTrip.getId(), 0, false, 0.0, "Véhicule non trouvé");
            }

            // 2. Récupérer la dernière position GPS
            Optional<GPSLocation> lastLocOpt = gpsRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicle.getId());

            if (lastLocOpt.isEmpty()) {
                logger.warn("Aucune position GPS trouvée pour le véhicule ID: {}", vehicle.getId());
                return new ETAResponseDTO(
                        activeTrip.getId(),
                        0,
                        false,
                        0.0,
                        "En attente de position GPS"
                );
            }

            GPSLocation lastLoc = lastLocOpt.get();

            // 3. Récupérer la destination depuis le premier étudiant du parent
            Optional<Student> studentOpt = studentRepository.findFirstByParentId(parentId);

            if (studentOpt.isEmpty()) {
                logger.warn("Aucun étudiant trouvé pour le parent ID: {}", parentId);
                return new ETAResponseDTO(
                        activeTrip.getId(),
                        0,
                        false,
                        0.0,
                        "Destination non configurée"
                );
            }

            Student student = studentOpt.get();

            // 4. Coordonnées de destination (école)
            // En production, ces coordonnées devraient être stockées dans l'entité School
            double schoolLat = 36.8065;  // Exemple: Tunis
            double schoolLon = 10.1815;  // Exemple: Tunis

            // Si l'étudiant a une école spécifiée, utiliser ses coordonnées
            // Pour l'instant, on utilise des coordonnées par défaut

            // 5. Calculer la distance
            double distanceKm = calculateDistance(
                    lastLoc.getLatitude(),
                    lastLoc.getLongitude(),
                    schoolLat,
                    schoolLon
            );

            // 6. Calculer l'ETA en minutes
            int estimatedMinutes = (int) ((distanceKm / AVERAGE_SPEED_KMH) * 60);

            // 7. Vérifier si le bus est proche (5 minutes ou moins)
            boolean isClose = estimatedMinutes <= 5;

            // 8. Si le bus est proche, envoyer l'alarme 5 minutes
            if (isClose && student.getParent() != null) {
                try {
                    // CORRECTION: Passer l'ID du parent comme deuxième argument
                    notificationService.sendFiveMinuteAlarm(activeTrip.getId(), student.getParent().getId());
                } catch (Exception e) {
                    logger.error("Erreur lors de l'envoi de l'alarme 5 minutes: {}", e.getMessage());
                }
            }

            // 9. Créer la réponse
            ETAResponseDTO response = new ETAResponseDTO(
                    activeTrip.getId(),
                    estimatedMinutes,
                    isClose,
                    Math.round(distanceKm * 100.0) / 100.0,
                    "ETA calculé avec succès"
            );

            logger.info("ETA calculé pour parent {}: {} minutes, distance: {} km",
                    parentId, estimatedMinutes, distanceKm);

            return response;

        } catch (Exception e) {
            logger.error("Erreur lors du calcul de l'ETA pour le parent ID: {}", parentId, e);
            return new ETAResponseDTO(
                    null,
                    0,
                    false,
                    0.0,
                    "Erreur lors du calcul: " + e.getMessage()
            );
        }
    }

    /**
     * Mise à jour proactive (Push) lors de la réception de données GPS (Kafka)
     */
    public void updateETAForVehicle(Long vehicleId, double currentLat, double currentLon) {
        try {
            Optional<Trip> activeTripOpt = tripRepository.findActiveTripByVehicle(vehicleId);

            if (activeTripOpt.isEmpty()) {
                logger.debug("Aucun trajet actif pour le véhicule ID: {}", vehicleId);
                return;
            }

            Trip trip = activeTripOpt.get();

            // Coordonnées de l'école (par défaut)
            double schoolLat = 36.8065;  // Tunis
            double schoolLon = 10.1815;  // Tunis

            // Calculer la distance
            double distance = calculateDistance(currentLat, currentLon, schoolLat, schoolLon);
            int estimatedMinutes = (int) ((distance / AVERAGE_SPEED_KMH) * 60);

            logger.debug("Véhicule {}: Distance restante {}km, ETA {} min",
                    vehicleId, distance, estimatedMinutes);

            // Alarme si le bus entre dans la zone des 5 minutes
            if (estimatedMinutes <= 5) {
                try {
                    // CORRECTION: On a besoin de l'ID du parent pour envoyer l'alarme
                    // Récupérer les parents associés à ce véhicule
                    List<Student> studentsInVehicle = studentRepository.findByVehicleId(vehicleId);

                    for (Student student : studentsInVehicle) {
                        if (student.getParent() != null) {
                            // CORRECTION: Passer l'ID du parent comme deuxième argument
                            notificationService.sendFiveMinuteAlarm(trip.getId(), student.getParent().getId());
                            logger.info("Alarme 5 minutes envoyée au parent {} pour le trajet {}",
                                    student.getParent().getId(), trip.getId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors de l'envoi de l'alarme pour le trajet {}", trip.getId(), e);
                }
            }

            // Log supplémentaire pour les distances intermédiaires
            if (estimatedMinutes <= 10) {
                logger.info("Véhicule {}: Arrivée dans {} minutes", vehicleId, estimatedMinutes);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'ETA pour le véhicule ID: {}", vehicleId, e);
        }
    }

    /**
     * Calcul de l'ETA pour un véhicule spécifique
     */
    public ETAResponseDTO calculateETAForVehicle(Long vehicleId) {
        try {
            Optional<Trip> activeTripOpt = tripRepository.findActiveTripByVehicle(vehicleId);

            if (activeTripOpt.isEmpty()) {
                return new ETAResponseDTO(null, 0, false, 0.0, "Aucun trajet actif pour ce véhicule");
            }

            Trip activeTrip = activeTripOpt.get();

            // Récupérer la dernière position GPS
            Optional<GPSLocation> lastLocOpt = gpsRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);

            if (lastLocOpt.isEmpty()) {
                return new ETAResponseDTO(
                        activeTrip.getId(),
                        0,
                        false,
                        0.0,
                        "Aucune position GPS disponible"
                );
            }

            GPSLocation lastLoc = lastLocOpt.get();

            // Coordonnées de destination
            double schoolLat = 36.8065;
            double schoolLon = 10.1815;

            // Calculer la distance
            double distanceKm = calculateDistance(
                    lastLoc.getLatitude(),
                    lastLoc.getLongitude(),
                    schoolLat,
                    schoolLon
            );

            // Calculer l'ETA
            int estimatedMinutes = (int) ((distanceKm / AVERAGE_SPEED_KMH) * 60);

            return new ETAResponseDTO(
                    activeTrip.getId(),
                    estimatedMinutes,
                    estimatedMinutes <= 5,
                    Math.round(distanceKm * 100.0) / 100.0,
                    "ETA calculé avec succès"
            );

        } catch (Exception e) {
            logger.error("Erreur lors du calcul de l'ETA pour le véhicule ID: {}", vehicleId, e);
            return new ETAResponseDTO(
                    null,
                    0,
                    false,
                    0.0,
                    "Erreur: " + e.getMessage()
            );
        }
    }

    /**
     * Formule de Haversine pour la distance entre deux points (en km)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculer la distance entre deux points GPS
     */
    public double calculateDistanceBetweenPoints(GPSLocation point1, GPSLocation point2) {
        return calculateDistance(
                point1.getLatitude(),
                point1.getLongitude(),
                point2.getLatitude(),
                point2.getLongitude()
        );
    }

    /**
     * Vérifier si le véhicule est proche de la destination
     */
    public boolean isVehicleCloseToDestination(Long vehicleId, double destinationLat, double destinationLon, int thresholdMinutes) {
        try {
            Optional<GPSLocation> lastLocOpt = gpsRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);

            if (lastLocOpt.isEmpty()) {
                return false;
            }

            GPSLocation lastLoc = lastLocOpt.get();
            double distanceKm = calculateDistance(
                    lastLoc.getLatitude(),
                    lastLoc.getLongitude(),
                    destinationLat,
                    destinationLon
            );

            int estimatedMinutes = (int) ((distanceKm / AVERAGE_SPEED_KMH) * 60);
            return estimatedMinutes <= thresholdMinutes;

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de proximité pour le véhicule ID: {}", vehicleId, e);
            return false;
        }
    }

    /**
     * Version simplifiée pour mettre à jour l'ETA sans alarme
     */
    public void updateETAWithoutAlarm(Long vehicleId, double currentLat, double currentLon) {
        try {
            Optional<Trip> activeTripOpt = tripRepository.findActiveTripByVehicle(vehicleId);

            if (activeTripOpt.isEmpty()) {
                return;
            }

            Trip trip = activeTripOpt.get();
            double schoolLat = 36.8065;
            double schoolLon = 10.1815;
            double distance = calculateDistance(currentLat, currentLon, schoolLat, schoolLon);
            int estimatedMinutes = (int) ((distance / AVERAGE_SPEED_KMH) * 60);

            logger.debug("ETA mis à jour pour véhicule {}: {} minutes", vehicleId, estimatedMinutes);

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'ETA: {}", e.getMessage());
        }
    }
}
