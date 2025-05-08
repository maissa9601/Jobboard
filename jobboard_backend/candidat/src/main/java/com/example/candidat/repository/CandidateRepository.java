package com.example.candidat.repository;

import com.example.candidat.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidat, Long> {
    Optional<Object> findByUserId(Long userId);
}
