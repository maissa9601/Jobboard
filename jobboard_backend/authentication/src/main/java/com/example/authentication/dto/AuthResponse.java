package com.example.authentication.dto;

import com.example.authentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String email;
    private Role role;
    private String token;
}
