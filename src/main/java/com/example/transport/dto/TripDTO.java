package com.example.transport.dto;

import com.example.transport.entitie.Trip;

import java.time.LocalDateTime;

public class TripDTO {

    private Long id;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long vehicleId;

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.status = trip.getStatus().name();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.vehicleId = trip.getVehicle().getId();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TripDTO() {
    }
}
