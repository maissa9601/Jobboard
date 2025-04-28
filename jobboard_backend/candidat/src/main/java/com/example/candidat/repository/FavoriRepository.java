package com.example.candidat.repository;

import com.example.candidat.model.Favori;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByUserId(Long userId);
}
