package com.example.admin.service;

import com.example.admin.Dto.AdminUpdateRequest;
import com.example.admin.model.Admin;
import com.example.admin.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;



@Service
public class AdmineService {

    @Autowired
    private AdminRepository adminRepository;


    @Value("${upload.dir}")
    private String uploadDir;

    public Admin createProfile(Long userId, String email) {
        Admin admin = new Admin();
        admin.setUserId(userId);
        admin.setEmail(email);
        return adminRepository.save(admin);
    }

    public void updateProfile(Long userId, AdminUpdateRequest request) {
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getProfilePhoto() != null && !request.getProfilePhoto().isEmpty()) {
            String photoUrl = uploadPhoto(userId, request.getProfilePhoto());
            admin.setPhotoUrl(photoUrl);
        }

        adminRepository.save(admin);
    }

    public String uploadPhoto(Long userId, MultipartFile file) {
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String photoUrl = uploadFile(file, "admins");
        admin.setPhotoUrl(photoUrl);
        adminRepository.save(admin);

        return photoUrl;
    }

    private String uploadFile(MultipartFile file, String subDir) {
        try {
            // Nettoyage du nom du fichier
            String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
            String fileName = UUID.randomUUID() + "_" + originalFileName;

            // Ex: admin/uploads/admins/
            Path uploadPath = Paths.get(uploadDir).resolve(subDir);
            Files.createDirectories(uploadPath); // Crée les dossiers si pas existants

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retourne une URL statique (à exposer avec ResourceHandler)
            return "/uploads/" + subDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public Optional<Admin> getProfile(Long userId) {
        return adminRepository.findByUserId(userId);
    }

}
