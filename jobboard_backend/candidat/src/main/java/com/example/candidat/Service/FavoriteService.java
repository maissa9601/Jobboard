package com.example.candidat.Service;

import com.example.candidat.model.Favori;
import com.example.candidat.repository.FavoriRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriRepository favoriteRepository;

    public FavoriteService(FavoriRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Favori addFavorite(Favori favorite) {
        return favoriteRepository.save(favorite);
    }

    public List<Favori> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public void removeFavorite(Long id) {
        favoriteRepository.deleteById(id);
    }
}
