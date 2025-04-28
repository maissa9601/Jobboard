package com.example.candidat.Controller;

import com.example.candidat.model.Candidat;
import com.example.candidat.model.Favori;
import com.example.candidat.Service.CandidatService;
import com.example.candidat.Service.FavoriteService;
import com.example.candidat.dto.FavoriteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidats")
public class CandidatController {

    private final CandidatService candidatService;
    private final FavoriteService favoriteService;

    public CandidatController(CandidatService candidatService, FavoriteService favoriteService) {
        this.candidatService = candidatService;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<Candidat> createProfile(@RequestBody Candidat profile) {
        Long userId = extractUserIdFromToken();
        profile.setUserId(userId);
        return ResponseEntity.ok(candidatService.createProfile(profile));
    }

    @GetMapping("/profiles/me")
    public ResponseEntity<Candidat> getMyProfile() {
        Long userId = extractUserIdFromToken();
        return candidatService.getProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profiles")
    public ResponseEntity<Candidat> updateProfile(@RequestBody Candidat profile) {
        Long userId = extractUserIdFromToken();
        profile.setUserId(userId);
        return ResponseEntity.ok(candidatService.updateProfile(profile));
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

    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
