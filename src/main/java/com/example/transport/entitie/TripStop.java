package com.example.transport.entitie;

import com.example.transport.entitie.enumeration.TripStopStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_stop")
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -------- Relations -------- */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    /* -------- Ordre de passage -------- */
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    /* -------- Temps -------- */
    @Column(name = "scheduled_arrival")
    private LocalDateTime scheduledArrival;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "child_pickup_time")
    private LocalDateTime childPickupTime;

    /* -------- Statut -------- */
    @Enumerated(EnumType.STRING)
    private TripStopStatus status;

    private String notes;

    /* -------- Getters / Setters -------- */

    public Long getId() { return id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Parent getParent() { return parent; }
    public void setParent(Parent parent) { this.parent = parent; }

    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }

    public LocalDateTime getScheduledArrival() { return scheduledArrival; }
    public void setScheduledArrival(LocalDateTime scheduledArrival) { this.scheduledArrival = scheduledArrival; }

    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }

    public LocalDateTime getChildPickupTime() { return childPickupTime; }
    public void setChildPickupTime(LocalDateTime childPickupTime) { this.childPickupTime = childPickupTime; }

    public TripStopStatus getStatus() { return status; }
    public void setStatus(TripStopStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
