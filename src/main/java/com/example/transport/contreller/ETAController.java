package com.example.transport.contreller ;

import com.example.transport.dto.ETAResponseDTO;
import com.example.transport.services.ETAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eta")
@CrossOrigin(origins = "http://localhost:3000")
public class ETAController {

    @Autowired
    private ETAService etaService;

    /**
     * Calcul de l'ETA pour un parent (Pull - appel REST)
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ETAResponseDTO> getETAForParent(@PathVariable Long parentId) {
        try {
            ETAResponseDTO eta = etaService.calculateETAForParent(parentId);
            return ResponseEntity.ok(eta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ETAResponseDTO(null, 0, false, 0.0, "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Calcul de l'ETA pour un véhicule (pour admin/conducteur)
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ETAResponseDTO> getETAForVehicle(@PathVariable Long vehicleId) {
        try {
            ETAResponseDTO eta = etaService.calculateETAForVehicle(vehicleId);
            return ResponseEntity.ok(eta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ETAResponseDTO(null, 0, false, 0.0, "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Simuler l'arrivée du bus (pour tests)
     */
    @PostMapping("/simulate-arrival/{parentId}")
    public ResponseEntity<String> simulateBusArrival(@PathVariable Long parentId) {
        try {
            // Cette méthode serait normalement appelée par le tracking
            // Mais pour la simulation, on l'expose
            ETAResponseDTO eta = etaService.calculateETAForParent(parentId);

            if (eta.isClose()) {
                return ResponseEntity.ok("Alarme 5 minutes déclenchée pour le parent " + parentId);
            } else {
                return ResponseEntity.ok("Bus encore loin. ETA: " + eta.getEstimatedMinutes() + " minutes");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
