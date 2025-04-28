package com.example.candidat.Service;

import com.example.candidat.model.Candidat;
import com.example.candidat.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidatService {

    private final CandidateRepository candidatProfileRepository;

    public CandidatService(CandidateRepository candidatProfileRepository) {
        this.candidatProfileRepository = candidatProfileRepository;
    }

    public Candidat createProfile(Candidat profile) {
        return candidatProfileRepository.save(profile);
    }

    public Optional<Candidat> getProfile(Long userId) {
        return candidatProfileRepository.findById(userId);
    }

    public Candidat updateProfile(Candidat profile) {
        return candidatProfileRepository.save(profile);
    }
}
