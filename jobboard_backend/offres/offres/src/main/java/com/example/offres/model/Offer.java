package com.example.offres.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class Offer {
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
    private String experience;
    private Date published;
    private Date expires;
    private String url;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int views = 0;


    public void incrementViews() {
        this.views++;
    }



}
