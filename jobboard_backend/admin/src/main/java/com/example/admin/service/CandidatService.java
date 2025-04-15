package com.example.admin.service;

import com.example.admin.model.Candidat;
import com.example.admin.repository.CandidatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatService {
    private static CandidatRepository candidatRepository;

    public CandidatService(CandidatRepository candidatRepository) {
        CandidatService.candidatRepository = candidatRepository;
    }

    public static List<Candidat> getAllCandidats() {

        return candidatRepository.findAllCandidats();
    }

    public static Optional<Candidat> getCandidatrById(Long id) {

            return candidatRepository.findById(id);
        }

    public static void deleteCandidat(Long id) {
        candidatRepository.deleteById(id);
    }


    public Optional<Candidat> promoteToAdmin(Long id) {
        Optional<Candidat> candidatOpt = candidatRepository.findById(id);
        if (candidatOpt.isEmpty()) {
            return Optional.empty();
        }

        Candidat candidat = candidatOpt.get();
        candidat.setRole("ADMIN");
        return Optional.of(candidatRepository.save(candidat));
    }



}
