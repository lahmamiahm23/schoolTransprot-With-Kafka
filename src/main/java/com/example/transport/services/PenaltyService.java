package com.example.transport.services;

import com.example.transport.Repositories.ParentRepository;
import com.example.transport.Repositories.PenaltyRepository;
import com.example.transport.Repositories.TripStopRepository;
import com.example.transport.entitie.Parent;
import com.example.transport.entitie.Penalty;
import com.example.transport.entitie.TripStop;
import com.example.transport.entitie.enumeration.PenaltyStatus;
import com.example.transport.entitie.enumeration.TripStopStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private ParentRepository parentRepository;


    @Autowired
    private TripStopRepository tripStopRepository;

    /**
     * Marquer une pénalité comme payée
     */
    public void markAsPaid(Long penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new RuntimeException("Pénalité introuvable avec id: " + penaltyId));
        penalty.setStatus(PenaltyStatus.PAID);
        penaltyRepository.save(penalty);
    }

    /**
     * Confirmer que le parent a récupéré l'enfant à temps
     * Supprime ou marque la pénalité correspondante
     */
    public void clearPenaltyForOnTimePickup(Long parentId, Long tripStopId) {
        TripStop tripStop = tripStopRepository.findById(tripStopId)
                .orElseThrow(() -> new RuntimeException("Arrêt de trajet introuvable avec id: " + tripStopId));

        // Vérifie que c'est le bon parent
        if (!tripStop.getParent().getId().equals(parentId)) {
            throw new RuntimeException("Parent non autorisé pour cet arrêt");
        }

        // Marquer le pickup comme effectué
        tripStop.setChildPickupTime(LocalDateTime.now());
        tripStop.setStatus(TripStopStatus.COMPLETED);
        tripStopRepository.save(tripStop);

        // Supprimer ou marquer la pénalité si elle existe
        List<Penalty> penalties = penaltyRepository.findByParentId(parentId);
        for (Penalty p : penalties) {
            if (p.getStatus() == PenaltyStatus.PENDING) {
                p.setStatus(PenaltyStatus.CANCELLED); // ou supprimer selon ta logique
                penaltyRepository.save(p);
            }
        }
    }
    public Penalty applyPenalty(Parent parent) {

        Penalty penalty = new Penalty();
        penalty.setParent(parent);
        penalty.setReason("Retard ou absence lors du ramassage");
        penalty.setAmount(0.0); // Peut être défini selon ta règle métier
        penalty.setStatus(PenaltyStatus.PENDING); // Valeur valide dans ton enum
        penalty.setDateIssued(LocalDateTime.now());

        return penaltyRepository.save(penalty);
    }

    /**
     * Permet de créer une pénalité personnalisée pour un parent donné
     */
    public Penalty issuePenalty(Long parentId, String reason, Double amount) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent non trouvé avec l'id : " + parentId));

        Penalty penalty = new Penalty();
        penalty.setParent(parent);
        penalty.setReason(reason);
        penalty.setAmount(amount);
        penalty.setStatus(PenaltyStatus.PENDING); // Corrigé ici
        penalty.setDateIssued(LocalDateTime.now());

        return penaltyRepository.save(penalty);
    }

    /**
     * Récupère la liste des pénalités pour un parent spécifique.
     */
    public List<Penalty> getPenaltiesByParent(Long parentId) {
        if (!parentRepository.existsById(parentId)) {
            throw new RuntimeException("Impossible de récupérer les pénalités : Parent introuvable.");
        }
        return penaltyRepository.findByParentId(parentId);
    }

    /**
     * Met à jour le statut d'une pénalité (ex: payer la pénalité)
     */
    public Penalty updatePenaltyStatus(Long penaltyId, PenaltyStatus newStatus) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new RuntimeException("Pénalité introuvable."));

        penalty.setStatus(newStatus);
        return penaltyRepository.save(penalty);
    }
}
