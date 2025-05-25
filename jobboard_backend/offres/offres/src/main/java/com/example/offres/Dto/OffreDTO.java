package com.example.offres.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OffreDTO {
    private String title;
    private String description;
    private String location;
    private String contractType;
    private String offerUrl;
}