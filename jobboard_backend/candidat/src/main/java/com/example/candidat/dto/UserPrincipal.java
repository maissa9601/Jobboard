package com.example.candidat.dto;

public class UserPrincipal {
    private Long userId;
    private String email;

    public UserPrincipal(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}

