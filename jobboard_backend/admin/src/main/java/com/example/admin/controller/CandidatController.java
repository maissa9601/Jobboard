package com.example.admin.controller;

import com.example.admin.model.Candidat;
import com.example.admin.service.CandidatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class CandidatController {

    private final CandidatService candidatService;


    public CandidatController(CandidatService candidatService) {
        this.candidatService = candidatService;
    }


    @GetMapping("/candidats")
    public List<Candidat> getAllCandidats() {
        return candidatService.getAllCandidats(); // Appel correct du service
    }


    @PostMapping("/candidats/{id}/promote")
    public ResponseEntity<Candidat> promoteToAdmin(@PathVariable Long id) {
        return candidatService.promoteToAdmin(id)
                .map(updated -> ResponseEntity.ok(updated))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/candidats/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
        Optional<Candidat> existingCandidat = CandidatService.getCandidatrById(id);
        if (existingCandidat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CandidatService.deleteCandidat(id);
        return ResponseEntity.ok().build();
    }

}
