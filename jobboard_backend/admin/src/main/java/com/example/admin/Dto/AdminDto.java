package com.example.admin.Dto;


import lombok.*;
@Data
public class AdminDto {

    private Long id;
    private String email;
    private String password;
    private String role = "ADMIN";
    private boolean enabled = true;
}
