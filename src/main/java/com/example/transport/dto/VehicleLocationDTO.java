package com.example.transport.dto;

import java.time.LocalDateTime;

public class VehicleLocationDTO {
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;

    public VehicleLocationDTO() {}

    public VehicleLocationDTO(Long vehicleId, Double latitude, Double longitude, LocalDateTime timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters / Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
