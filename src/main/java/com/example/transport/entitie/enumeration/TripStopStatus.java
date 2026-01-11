package com.example.transport.entitie.enumeration;

public enum TripStopStatus {
    SCHEDULED,      // Planifié
    WAITING,        // En attente (bus arrivé)
    COMPLETED,      // Enfant embarqué
    LATE_BOARDING,  // Embarcation tardive (pénalité)
    NO_SHOW,        // Parent absent
    CANCELLED       // Annulé
}
