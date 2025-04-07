package com.example.admin.repository;




import com.example.admin.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long>{
    @Query("SELECT a FROM Admin a WHERE a.role = 'CANDIDAT'")
    List<Candidat> findAllCandidats();
}
