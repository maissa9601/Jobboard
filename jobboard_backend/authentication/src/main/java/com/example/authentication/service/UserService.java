package com.example.authentication.service;


import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import com.example.authentication.repository.AdminRepository;
import com.example.authentication.repository.CandidatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final AdminRepository adminRepository;
    private final CandidatRepository candidatRepository;

    public UserService(AdminRepository adminRepository, CandidatRepository candidatRepository) {
        this.adminRepository = adminRepository;
        this.candidatRepository = candidatRepository;
    }

    public List<User> getAllAdmins() {
        return adminRepository.findAllAdmins();
    }

    public List<User> getAllCandidats() {
        return candidatRepository.findAllCandidats();
    }

    public Optional<User> getCandidatById(Long id) {
        return candidatRepository.findById(id);
    }

    public void deleteCandidat(Long id) {
        candidatRepository.deleteById(id);
    }

    public Optional<User> promoteToAdmin(Long id) {
        Optional<User> candidatOpt = candidatRepository.findById(id);
        if (candidatOpt.isEmpty()) {
            return Optional.empty();
        }

        User candidat = candidatOpt.get();
        candidat.setRole(Role.valueOf("ADMIN"));
        return Optional.of(candidatRepository.save(candidat));
    }
}
