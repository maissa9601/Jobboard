package com.example.candidat.Service;

import com.example.candidat.dto.CandidatUpdateRequest;
import com.example.candidat.model.Candidat;
import com.example.candidat.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class CandidatService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final CandidateRepository candidatProfileRepository;

    public CandidatService(CandidateRepository candidatProfileRepository) {
        this.candidatProfileRepository = candidatProfileRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(uploadDir));
    }

    public Candidat createProfile(Candidat profile) {
        return candidatProfileRepository.save(profile);
    }

    public Optional<Object> getProfile(Long userId) {
        return candidatProfileRepository.findByUserId(userId);
    }

    public void updateProfile(Long userId, CandidatUpdateRequest request) {
        Candidat candidat = candidatProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));

        candidat.setFullName(request.getFullName());
        candidat.setBio(request.getBio());
        candidat.setSkills(request.getSkills());
        candidat.setLanguages(request.getLanguages());

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoUrl = uploadPhoto(userId, request.getPhoto());
            candidat.setPhotoUrl(photoUrl);
        }

        if (request.getCv() != null && !request.getCv().isEmpty()) {
            String cvUrl = uploadCv(userId, request.getCv());
            candidat.setCvUrl(cvUrl);
        }

        candidatProfileRepository.save(candidat);
    }

    public String uploadPhoto(Long userId, MultipartFile file) {
        return uploadFile(file, "photos");
    }

    public String uploadCv(Long userId, MultipartFile file) {
        return uploadFile(file, "cvs");
    }

    private String uploadFile(MultipartFile file, String subDir) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir + File.separator + subDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}
