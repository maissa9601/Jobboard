package com.example.candidat.Service;

import com.example.candidat.model.Favori;
import com.example.candidat.repository.FavoriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;


@Service
public class FavoriteService {

    @Autowired
    private FavoriRepository favoriRepository;

    public Favori saveFavori(Favori favori) {
        Optional<Favori> existing = favoriRepository.findByUserIdAndOfferId(favori.getUserId(), favori.getOfferId());
        if (existing.isPresent()) {
            return existing.get();
        }
        return favoriRepository.save(favori);
    }

    public List<Favori> getFavorisByUserId(Long userId) {
        return favoriRepository.findByUserId(userId);
    }

    public void deleteFavori(Long userId, Long offerId) {
        favoriRepository.findByUserIdAndOfferId(userId, offerId)
                .ifPresent(favoriRepository::delete);
    }

}

