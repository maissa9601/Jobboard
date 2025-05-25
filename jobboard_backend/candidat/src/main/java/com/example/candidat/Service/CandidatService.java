package com.example.candidat.Service;

import com.example.candidat.dto.CandidatUpdateRequest;
import com.example.candidat.model.Alerte;
import com.example.candidat.model.Candidat;
import com.example.candidat.repository.AlerteRepository;
import com.example.candidat.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CandidatService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final CandidateRepository candidatProfileRepository;
    private final AlerteRepository alerRepository;

    public CandidatService(CandidateRepository candidatProfileRepository, AlerteRepository favoriProfileRepository) {
        this.candidatProfileRepository = candidatProfileRepository;
        this.alerRepository = favoriProfileRepository;
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
        candidat.setEmail(request.getEmail());
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
    public void marquerCommeLue(Long id) {
        Alerte alerte = alerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alerte.setLu(true);
        alerRepository.save(alerte);
    }
    public void deleteAlerte(Long userId, Long alerteId) {
        alerRepository.findByCandidatIdAndId(userId, alerteId)
                .ifPresent(alerRepository::delete);
    }




    public String uploadPhoto(Long userId, MultipartFile file) {
        String photoUrl = uploadFile(file, "photos");

        // Récupérer le candidat et mettre à jour son URL photo
        Candidat candidat = candidatProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));

        candidat.setPhotoUrl(photoUrl);  // Assure-toi que le champ existe dans l'entité
        candidatProfileRepository.save(candidat);

        return photoUrl;
    }

    public String uploadCv(Long userId, MultipartFile file) {
        String cvUrl = uploadFile(file, "cvs");

        Candidat candidat = candidatProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));

        candidat.setCvUrl(cvUrl);  // Assure-toi que le champ existe dans l'entité
        candidatProfileRepository.save(candidat);

        return cvUrl;
    }

    private String uploadFile(MultipartFile file, String subDir) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).resolve(subDir);

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
    public List<Alerte> getAlertesByUserId(Long userId) {
        return alerRepository.findByCandidatId(userId);
    }





}
