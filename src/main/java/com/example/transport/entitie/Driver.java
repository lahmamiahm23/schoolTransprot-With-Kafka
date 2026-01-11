package com.example.transport.entitie;

import com.example.transport.entitie.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "driver")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Driver extends User {

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"driver", "students", "trips", "locationHistory"})
    private Vehicle vehicle;

    // Constructeurs
    public Driver() {
        super();
    }

    public Driver(String licenseNumber) {
        super();
        this.licenseNumber = licenseNumber;
    }

    public Driver(String firstName, String lastName, String email, String phone,
                  String password, String licenseNumber) {
        super(firstName, lastName, email, phone, password, UserRole.DRIVER);
        this.licenseNumber = licenseNumber;
    }

    // Constructeur avec vehicle (à utiliser avec prudence)
    public Driver(String firstName, String lastName, String email, String phone,
                  String password, String licenseNumber, Vehicle vehicle) {
        super(firstName, lastName, email, phone, password, UserRole.DRIVER);
        this.licenseNumber = licenseNumber;
        this.vehicle = vehicle;
    }

    // Getters et Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
