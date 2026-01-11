package com.example.transport.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArrivalDTO {
    private Long stopId;
    private Long parentId;
    private LocalDateTime arrivalTime;
    private String message;

    public ArrivalDTO(Long stopId, Long parentId, LocalDateTime arrivalTime, String message) {
        this.stopId = stopId;
        this.parentId = parentId;
        this.arrivalTime = arrivalTime;
        this.message = message;
    }
}
