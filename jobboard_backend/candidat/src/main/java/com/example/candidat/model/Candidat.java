package com.example.candidat.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Candidat {

    @Id
    private Long userId;
    private String fullName;
    private String bio;
    private String skills;
    private String cvUrl;

}
