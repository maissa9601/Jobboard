package com.example.admin.security;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserPrincipal {
    private Long userId;
    private String email;

    public UserPrincipal(Long id, String email) {
        this.userId = id;
        this.email = email;
    }


}