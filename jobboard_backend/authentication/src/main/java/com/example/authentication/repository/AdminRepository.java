package com.example.authentication.repository;
import com.example.authentication.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {

    @Query("SELECT a FROM User a WHERE a.role = 'ADMIN'")
    List<User> findAllAdmins();
}
