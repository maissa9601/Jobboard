package com.example.authentication.repository;

import com.example.authentication.dto.Candidat;
import com.example.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidatRepository extends JpaRepository<User, Long> {

    @Query("SELECT c FROM User c WHERE c.role = 'CANDIDAT'")
    List<User> findAllCandidats();
}
