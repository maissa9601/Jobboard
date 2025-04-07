package com.example.admin.repository;

import com.example.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a WHERE a.role = 'ADMIN'")
    List<Admin> findAllAdmins();
}
