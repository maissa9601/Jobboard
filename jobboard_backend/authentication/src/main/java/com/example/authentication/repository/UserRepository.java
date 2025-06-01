package com.example.authentication.repository;


import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByRole(Role role);
    @Query("SELECT c FROM User c WHERE c.role = 'CANDIDAT'")
    List<User> findAllCandidats();
    @Query("SELECT c FROM User c WHERE c.lastLogin >= :since AND c.role = 'CANDIDAT' ORDER BY c.lastLogin DESC")
    List<User> findRecentLogins(@Param("since") LocalDateTime since);


    @Query("SELECT a FROM User a WHERE a.role = 'ADMIN'")
    List<User> findAllAdmins();



}




