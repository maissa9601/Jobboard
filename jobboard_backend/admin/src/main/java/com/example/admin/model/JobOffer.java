package com.example.admin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String contractype;
    private String description;
    private String company;
    private String location;
    private Double salary;
    private String source;

}