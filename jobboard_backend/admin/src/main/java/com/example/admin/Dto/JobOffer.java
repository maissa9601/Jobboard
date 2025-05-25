package com.example.admin.Dto;

import lombok.Data;

import java.util.Date;

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
    private String experience;
    private Date published;
    private Date expires;
    private String url;

}