package com.example.admin.Dto;

import lombok.Data;

 @Data
public class JobOffer {

    private Long id;
    private String title;
    private String contractype;
    private String description;
    private String company;
    private String location;
    private Double salary;
    private String source;

}