package com.example.admin.Repository;

import com.example.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
     Optional<Admin> findByUserId(Long userId);



}


