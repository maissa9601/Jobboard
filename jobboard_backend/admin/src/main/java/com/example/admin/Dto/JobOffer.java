package com.example.admin.Dto;

import jakarta.persistence.*;
import lombok.Data;

 @Data
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