package com.example.candidat.repository;

import com.example.candidat.model.Favori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByUserId(Long userId);
    Optional<Favori> findByUserIdAndOfferId(Long userId, Long offerId);
}
