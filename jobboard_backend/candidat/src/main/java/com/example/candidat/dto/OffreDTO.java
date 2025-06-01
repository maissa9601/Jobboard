package com.example.candidat.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
@Getter
@Setter
public class OffreDTO {
        private String title;
        private String description;
        private String location;
        private String contractType;
        private String offerUrl;
        private int views;
}


