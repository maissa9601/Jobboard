package com.example.authentication.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidat {

        private Long id;
        private String email;
        private String password;
        private String enabled;
        private String role = "ADMIN";



}
