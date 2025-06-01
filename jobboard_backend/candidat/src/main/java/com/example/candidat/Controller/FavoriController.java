package com.example.candidat.Controller;

import com.example.candidat.Service.FavoriteService;
import com.example.candidat.dto.UserPrincipal;
import com.example.candidat.model.Favori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/candidat")
public class FavoriController {
    @Autowired
    private FavoriteService favoriteService;


    @PostMapping("/favorite/add")
    public ResponseEntity<Favori> addFavori(@RequestBody Favori favori) {
        Favori saved = favoriteService.saveFavori(favori);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/favorite/{userId}")
    public ResponseEntity<List<Favori>> getFavoris() {
        Long userId=extractUserIdFromToken();
        return ResponseEntity.ok(favoriteService.getFavorisByUserId(userId));
    }

    @DeleteMapping("/favorite/{userId}/{offerId}")
    public ResponseEntity<Void> removeFavori(@PathVariable Long offerId) {
        Long userId=extractUserIdFromToken();
        favoriteService.deleteFavori(userId, offerId);
        return ResponseEntity.noContent().build();
    }
    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getUserId();
        }
        return null;
    }



}
