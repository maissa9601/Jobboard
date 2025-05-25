package com.example.candidat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Candidat {

    @Id
    private Long userId;
    private String fullName;
    private String email;
    private String bio;
    private String cvUrl;
    private String photoUrl;
    @ElementCollection
    private List<String> skills;
    @ElementCollection
    private List<String> languages;
    @ElementCollection
    private List<String> preferredKeywords;
    private String preferredLocation;
    private String preferredContractType;



}
