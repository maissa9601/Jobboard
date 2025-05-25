package com.example.candidat.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CandidatUpdateRequest {
    private String fullName;
    private String email;
    private String bio;
    private MultipartFile photo;
    private MultipartFile cv;
    private List<String> skills;
    private List<String> languages;
}

