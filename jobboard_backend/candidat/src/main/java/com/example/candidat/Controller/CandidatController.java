package com.example.candidat.Controller;

import com.example.candidat.dto.CandidatUpdateRequest;
import com.example.candidat.model.Candidat;
import com.example.candidat.model.Favori;
import com.example.candidat.Service.CandidatService;
import com.example.candidat.Service.FavoriteService;
import com.example.candidat.dto.FavoriteRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/candidat")
public class CandidatController {

    private final CandidatService candidatService;
    private final FavoriteService favoriteService;

    public CandidatController(CandidatService candidatService, FavoriteService favoriteService) {
        this.candidatService = candidatService;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/profile/create")
    public ResponseEntity<Candidat> createProfile(@RequestBody Candidat profile) {
        Long userId = extractUserIdFromToken();
        profile.setUserId(userId);
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
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/profile/photo")
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
        return Long.parseLong(authentication.getName());
    }


    @PostMapping("/favorites")
    public ResponseEntity<Favori> addFavorite(@RequestBody FavoriteRequest favoriteRequest) {
        Long userId = extractUserIdFromToken();
        Favori favorite = Favori.builder()
                .userId(userId)
                .offerId(favoriteRequest.getOfferId())
                .offerUrl(favoriteRequest.getOfferUrl())
                .build();
        return ResponseEntity.ok(favoriteService.addFavorite(favorite));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Favori>> getMyFavorites() {
        Long userId = extractUserIdFromToken();
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long favoriteId) {
        favoriteService.removeFavorite(favoriteId);
        return ResponseEntity.ok().build();
    }





}
