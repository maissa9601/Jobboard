package com.example.admin.Dto;

import jakarta.persistence.*;
import lombok.*;
@Data
public class Candidat{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "Candidat";

    @Column(nullable = false)
    private boolean enabled = true;
}
