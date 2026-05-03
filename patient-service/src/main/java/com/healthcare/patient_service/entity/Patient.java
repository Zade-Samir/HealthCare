package com.healthcare.patient_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String phone;
    private Integer age;
    private String gender;
    private String address;
    private LocalDateTime createdAt;
    private boolean isDeleted = false;

    //to save the createdAt time
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
