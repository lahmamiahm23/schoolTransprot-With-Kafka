package com.example.transport.Repositories;

import com.example.transport.entitie.Trip;
import com.example.transport.entitie.enumeration.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /** Trajet actif (IN_PROGRESS) pour un véhicule */
    @Query("SELECT t FROM Trip t WHERE t.vehicle.id = :vehicleId AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findActiveTripByVehicle(@Param("vehicleId") Long vehicleId);

    /** Trajet actif (IN_PROGRESS) pour un parent via ses enfants */
    @Query("SELECT DISTINCT t FROM Trip t " +
            "JOIN t.vehicle v " +
            "JOIN Student s ON s.vehicle = v " +
            "WHERE s.parent.id = :parentId " +
            "AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findActiveTripByParentId(@Param("parentId") Long parentId);

    /** Tous les trajets par statut */
    List<Trip> findByStatus(TripStatus status);

    /** Tous les trajets d’un véhicule */
    List<Trip> findByVehicleIdOrderByStartTimeDesc(Long vehicleId);

    /** Dernier trajet d’un véhicule */
    @Query("SELECT t FROM Trip t WHERE t.vehicle.id = :vehicleId ORDER BY t.startTime DESC")
    Optional<Trip> findTopByVehicleIdOrderByStartTimeDesc(@Param("vehicleId") Long vehicleId);

    /** Trajets entre deux dates */
    @Query("SELECT t FROM Trip t WHERE t.startTime BETWEEN :startDate AND :endDate ORDER BY t.startTime DESC")
    List<Trip> findTripsBetweenDates(@Param("startDate") java.time.LocalDateTime startDate,
                                     @Param("endDate") java.time.LocalDateTime endDate);

    /** Compter les trajets actifs */
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = 'IN_PROGRESS'")
    long countActiveTrips();

    /** Vérifier si un véhicule a un trajet en cours */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Trip t WHERE t.vehicle.id = :vehicleId AND t.status = 'IN_PROGRESS'")
    boolean existsActiveTripByVehicleId(@Param("vehicleId") Long vehicleId);
}
