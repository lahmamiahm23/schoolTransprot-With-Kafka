package com.example.transport.kafka.consumer;

import com.example.transport.dto.LocationDTO;
import com.example.transport.services.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class GPSConsumer {

    @Autowired
    private TrackingService trackingService;

    @KafkaListener(topics = "school-bus-locations", groupId = "transport-group")
    public void consume(LocationDTO locationDTO) {
        try {
            // On délègue tout le traitement métier au TrackingService
            trackingService.processNewLocation(locationDTO);
        } catch (Exception e) {
            // Loguer l'erreur pour éviter que le consumer ne s'arrête
            System.err.println("Erreur de traitement GPS: " + e.getMessage());
        }
    }
}
