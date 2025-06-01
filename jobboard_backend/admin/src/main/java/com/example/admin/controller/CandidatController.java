package com.example.admin.controller;

import com.example.admin.Dto.Candidat;
import com.example.admin.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatController {

    private final CandidatService candidatService;

    @Autowired
    public CandidatController(CandidatService candidatService) {
        this.candidatService = candidatService;
    }

    @GetMapping("/candidats")
    public ResponseEntity<List<Candidat>> getAllCandidats() {
        return ResponseEntity.ok(candidatService.getAllCandidats());
    }

    @PostMapping("/candidats/{id}/promote")
    public ResponseEntity<Candidat> promote(@PathVariable Long id) {
        return ResponseEntity.ok(candidatService.promote(id));
    }

    @DeleteMapping("/candidats/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
        candidatService.deleteCandidat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        return ResponseEntity.ok(candidatService.getUserStats());
    }

    @GetMapping("/candidats/recent")
    public ResponseEntity<List<Candidat>> getRecentCandidats() {
        return ResponseEntity.ok(candidatService.getRecentCandidats());
    }
}
