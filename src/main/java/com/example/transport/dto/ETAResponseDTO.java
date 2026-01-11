package com.example.transport.dto;

public class ETAResponseDTO {
    private Long tripId;
    private int estimatedMinutes;
    private boolean isClose;
    private double distanceKm;
    private String message;

    // Constructeurs
    public ETAResponseDTO() {}

    public ETAResponseDTO(Long tripId, int estimatedMinutes, boolean isClose, double distanceKm, String message) {
        this.tripId = tripId;
        this.estimatedMinutes = estimatedMinutes;
        this.isClose = isClose;
        this.distanceKm = distanceKm;
        this.message = message;
    }

    // Getters et Setters
    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ETAResponseDTO{" +
                "tripId=" + tripId +
                ", estimatedMinutes=" + estimatedMinutes +
                ", isClose=" + isClose +
                ", distanceKm=" + distanceKm +
                ", message='" + message + '\'' +
                '}';
    }
}
