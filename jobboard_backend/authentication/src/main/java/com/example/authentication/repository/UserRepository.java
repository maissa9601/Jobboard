package com.example.authentication.repository;

import com.example.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByEnabled(boolean enabled);

    boolean enabled(boolean enabled);

    Optional<User> findByActivationToken(String token);

    Optional<User> findByResetToken(String token);
}
