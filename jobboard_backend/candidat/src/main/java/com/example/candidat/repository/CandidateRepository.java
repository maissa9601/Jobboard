package com.example.candidat.repository;

import com.example.candidat.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidat, Long> {
}
