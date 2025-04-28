package com.example.admin.Dto;


import lombok.*;
@Data
public class Candidat{

    private Long id;
    private String email;
    private String password;
    private String role = "Candidat";
    private boolean enabled = true;
}
