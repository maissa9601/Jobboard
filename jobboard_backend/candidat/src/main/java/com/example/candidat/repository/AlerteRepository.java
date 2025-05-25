package com.example.candidat.repository;

import com.example.candidat.model.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findByCandidatId(Long id);
    Optional<Alerte> findByCandidatIdAndId(Long candidatId, Long id);


}

