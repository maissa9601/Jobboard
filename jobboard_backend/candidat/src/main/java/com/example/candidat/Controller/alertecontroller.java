package com.example.candidat.Controller;

import com.example.candidat.dto.CandidatMatchDto;
import com.example.candidat.dto.OffreDTO;
import com.example.candidat.Service.AlerteService;
import com.example.candidat.dto.ReclamationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidats")
@RequiredArgsConstructor
public class alertecontroller {

    private final AlerteService alerteService;


    @PostMapping("/match-candidates")
    public ResponseEntity<List<CandidatMatchDto>> findMatchingCandidates(@RequestBody OffreDTO offreDto) {
        List<CandidatMatchDto> matching = alerteService.findMatchingCandidatEmails(offreDto);
        return ResponseEntity.ok(matching);
    }

    @PostMapping("/reclamation")
    public ResponseEntity<String> envoyerReclamation(@RequestBody ReclamationEvent event) {
        alerteService.envoyerReclamation(event);
        return ResponseEntity.ok("Reclamation envoyée avec succès.");
    }

}
