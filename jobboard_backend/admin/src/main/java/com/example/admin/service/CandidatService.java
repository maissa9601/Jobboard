package com.example.admin.service;

import com.example.admin.model.Candidat;
import com.example.admin.repository.CandidatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CandidatService {
    private static CandidatRepository candidatRepository;

    public CandidatService(CandidatRepository candidatRepository) {
        this.candidatRepository = candidatRepository;
    }

    public static List<Candidat> getAllCandidats() {

        return candidatRepository.findAllCandidats();
    }
}
