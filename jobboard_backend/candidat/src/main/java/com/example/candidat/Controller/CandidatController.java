package com.example.candidat.Controller;

import com.example.candidat.dto.CandidatUpdateRequest;
import com.example.candidat.dto.UserPrincipal;
import com.example.candidat.model.Candidat;
import com.example.candidat.Service.CandidatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;




@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/candidat")
public class CandidatController {

    private final CandidatService candidatService;



    public CandidatController(CandidatService candidatService) {
        this.candidatService = candidatService;

    }
    @PostMapping("/profile/create")
    public ResponseEntity<Candidat> createProfile(@RequestBody Candidat profile) {
        Long userId = extractUserIdFromToken();
        profile.setUserId(userId);
        String email=extractEmailFromToken();
        profile.setEmail(email);
        return ResponseEntity.ok(candidatService.createProfile(profile));
    }

    @GetMapping("/profile/me")
    public ResponseEntity<Object> getMyProfile() {
        Long userId = extractUserIdFromToken();
        return candidatService.getProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(@ModelAttribute CandidatUpdateRequest request) {
        Long userId = extractUserIdFromToken();
        candidatService.updateProfile(userId, request);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));

    }

    @PostMapping("/profile/photo/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        Long userId = extractUserIdFromToken();
        String photoUrl = candidatService.uploadPhoto(userId, file);
        return ResponseEntity.ok(photoUrl);
    }

    @PostMapping("/profile/cv/upload")
    public ResponseEntity<String> uploadCv(@RequestParam("file") MultipartFile file) {
        Long userId = extractUserIdFromToken();

        String cvUrl = candidatService.uploadCv(userId, file);
        return ResponseEntity.ok(cvUrl);
    }

    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getUserId();
        }
        return null;
    }

    private String extractEmailFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getEmail();
        }
        return null;
    }



}
