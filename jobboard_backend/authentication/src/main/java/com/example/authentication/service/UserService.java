package com.example.authentication.service;


import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
  
        this.userRepository= userRepository;
    }

    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public List<User> getAllCandidats() {
        return userRepository.findAllCandidats();
    }

    public Optional<User> getCandidatById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteCandidat(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> promoteToAdmin(Long id) {
        Optional<User> candidatOpt = userRepository.findById(id);
        if (candidatOpt.isEmpty()) {
            return Optional.empty();
        }

        User candidat = candidatOpt.get();
        candidat.setRole(Role.valueOf("ADMIN"));
        return Optional.of(userRepository.save(candidat));
    }
    public long countAdmins() {
        return userRepository.countByRole(Role.valueOf("ADMIN"));
    }

    public long countCandidats() {
        return userRepository.countByRole(Role.valueOf("CANDIDAT"));
    }

    public List<User> getRecentCandidats() {
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        return userRepository.findRecentLogins(lastWeek);
    }


}
