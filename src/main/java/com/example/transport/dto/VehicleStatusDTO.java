package com.example.transport.dto;

import java.time.LocalDateTime;

public class VehicleStatusDTO {
    private Long vehicleId;
    private String plateNumber;
    private String model;
    private String driverName;
    private String driverPhone;
    private String currentTripStatus;
    private Long tripId;
    private Double lastLatitude;
    private Double lastLongitude;
    private LocalDateTime lastUpdate;
    private LocalDateTime startTime;
    private Integer studentCount;
    private Double speed;
    private String heading;
    private Boolean isOnline;
    private Double estimatedTimeArrival;

    // Constructeurs
    public VehicleStatusDTO() {}

    public VehicleStatusDTO(Long vehicleId, String plateNumber, String model,
                            String driverName, String currentTripStatus) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.model = model;
        this.driverName = driverName;
        this.currentTripStatus = currentTripStatus;
        this.isOnline = false;
        this.studentCount = 0;
    }

    // Getters & Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getCurrentTripStatus() {
        return currentTripStatus;
    }

    public void setCurrentTripStatus(String currentTripStatus) {
        this.currentTripStatus = currentTripStatus;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(Double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Double getEstimatedTimeArrival() {
        return estimatedTimeArrival;
    }

    public void setEstimatedTimeArrival(Double estimatedTimeArrival) {
        this.estimatedTimeArrival = estimatedTimeArrival;
    }

    // Méthode utilitaire pour calculer si le véhicule est récent
    public boolean isLocationRecent(int minutesThreshold) {
        if (lastUpdate == null) return false;
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(minutesThreshold);
        return lastUpdate.isAfter(thresholdTime);
    }

    // Méthode pour obtenir le statut textuel
    public String getStatusText() {
        if (!Boolean.TRUE.equals(isOnline)) {
            return "Hors ligne";
        }

        switch (currentTripStatus) {
            case "IN_PROGRESS":
                return "En route";
            case "SCHEDULED":
                return "Programmé";
            case "COMPLETED":
                return "Terminé";
            case "CANCELLED":
                return "Annulé";
            case "DELAYED":
                return "Retardé";
            default:
                return "Inconnu";
        }
    }

    // Méthode pour obtenir la couleur du statut
    public String getStatusColor() {
        if (!Boolean.TRUE.equals(isOnline)) {
            return "gray";
        }

        switch (currentTripStatus) {
            case "IN_PROGRESS":
                return "green";
            case "SCHEDULED":
                return "blue";
            case "COMPLETED":
                return "gray";
            case "CANCELLED":
                return "red";
            case "DELAYED":
                return "orange";
            default:
                return "black";
        }
    }

    @Override
    public String toString() {
        return "VehicleStatusDTO{" +
                "vehicleId=" + vehicleId +
                ", plateNumber='" + plateNumber + '\'' +
                ", driverName='" + driverName + '\'' +
                ", status='" + currentTripStatus + '\'' +
                ", isOnline=" + isOnline +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
