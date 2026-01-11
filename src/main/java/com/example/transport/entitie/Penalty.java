package com.example.transport.entitie;

import com.example.transport.entitie.enumeration.PenaltyStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Penalty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;
    private Double amount;
    private LocalDateTime dateIssued;

    @Enumerated(EnumType.STRING)
    private PenaltyStatus status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public Penalty() {
    }

    public Penalty(Long id, String reason, LocalDateTime dateIssued, Double amount, PenaltyStatus status, Parent parent) {
        this.id = id;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.amount = amount;
        this.status = status;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(LocalDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }

    public PenaltyStatus getStatus() {
        return status;
    }

    public void setStatus(PenaltyStatus status) {
        this.status = status;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
