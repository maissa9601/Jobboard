package com.example.admin.repository;




import com.example.admin.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long>{
    @Query("SELECT a FROM Candidat a WHERE a.role = 'CANDIDAT'")
    List<Candidat> findAllCandidats();
    Optional<Candidat> findById(Long id);


}
