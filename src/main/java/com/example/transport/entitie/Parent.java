package com.example.transport.entitie;

import com.example.transport.entitie.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parent")
public class Parent extends User {

    // Coordonnées du domicile fixées par l'Admin pour le Geofencing
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"parent", "vehicle"})
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("parent")
    private List<Penalty> penalties = new ArrayList<>();

    public Parent() { super(); }

    public Parent(String firstName, String lastName, String email, String phone, String password, Double latitude, Double longitude) {
        super(firstName, lastName, email, phone, password, UserRole.PARENT);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public List<Penalty> getPenalties() { return penalties; }
    public void setPenalties(List<Penalty> penalties) { this.penalties = penalties; }
}
