package com.example.transport.kafka.producer;

import com.example.transport.dto.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GPSProducer {

    private static final String TOPIC = "school-bus-locations";

    @Autowired
    private KafkaTemplate<String, LocationDTO> kafkaTemplate;

    public void sendLocation(LocationDTO location) {
        // Envoi du DTO au format JSON sur le topic Kafka
        this.kafkaTemplate.send(TOPIC, String.valueOf(location.getVehicleId()), location);
    }
}
