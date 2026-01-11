package com.example.transport.dto;

public class PenaltyDTO {
    private Long parentId;
    private String reason;
    private Double amount;

    // Constructeurs
    public PenaltyDTO() {}

    public PenaltyDTO(Long parentId, String reason, Double amount) {
        this.parentId = parentId;
        this.reason = reason;
        this.amount = amount;
    }

    // Getters et Setters
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
