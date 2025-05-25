package com.example.candidat.Controller;

import com.example.candidat.Service.CandidatService;
import com.example.candidat.Service.EmailService;
import com.example.candidat.dto.OffreDTO;
import com.example.candidat.dto.UserPrincipal;
import com.example.candidat.model.Alerte;
import com.example.candidat.model.Candidat;
import com.example.candidat.repository.AlerteRepository;
import com.example.candidat.repository.CandidateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/candidat")
public class OfferListnerController {

    private final CandidateRepository candidatRepository;
    private final AlerteRepository alerteRepository;
    private final EmailService emailService;
    private final CandidatService candidatService;

    public OfferListnerController(CandidateRepository candidatRepository, AlerteRepository alerteRepository, EmailService emailService, CandidatService candidatService) {
        this.candidatRepository = candidatRepository;
        this.alerteRepository = alerteRepository;
        this.emailService = emailService;
        this.candidatService = candidatService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> recevoirNouvelleOffre(@RequestBody OffreDTO offre) {
        List<Candidat> candidats = candidatRepository.findAll();

        try {
            for (Candidat c : candidats) {
                if (correspondAuxPreferences(c, offre)) {
                    Alerte alerte = new Alerte();
                    alerte.setCandidatId(c.getUserId());
                    alerte.setOfferTitle(offre.getTitle());
                    alerte.setOfferDescription(offre.getDescription());
                    alerte.setOfferUrl(offre.getOfferUrl());
                    alerte.setDateCreation(LocalDate.now());
                    alerte.setLu(false);

                    alerteRepository.save(alerte);
                    emailService.sendAlertEmail(c.getEmail(), offre);
                }
            }
            return ResponseEntity.ok(Collections.singletonMap("message", "Alertes envoyées avec succès."));
        }catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Une erreur est survenue lors de la création des alertes."));
        }

    }



private boolean correspondAuxPreferences(Candidat c, OffreDTO offre) {

    List<String> allKeywords = new ArrayList<>(c.getPreferredKeywords());
    if (c.getBio() != null) {
        allKeywords.addAll(Arrays.asList(c.getBio().toLowerCase().split("\\s+")));
    }

    boolean motsClefOk = allKeywords.stream().anyMatch(kw ->
            offre.getTitle().toLowerCase().contains(kw.toLowerCase()) ||
                    offre.getDescription().toLowerCase().contains(kw.toLowerCase())
    );

    boolean locationOk = c.getPreferredLocation() == null ||
            offre.getLocation().equalsIgnoreCase(c.getPreferredLocation());

    boolean contratOk = c.getPreferredContractType() == null ||
            offre.getContractType().equalsIgnoreCase(c.getPreferredContractType());

    return motsClefOk && locationOk && contratOk;
}
    @GetMapping("/alertes/{candidatId}")
    public ResponseEntity<List<Alerte>> getAlerts() {
        Long candidatId=extractUserIdFromToken();
        return ResponseEntity.ok(candidatService.getAlertesByUserId(candidatId));
    }
    @PutMapping("/alertes/{id}/lu")
    public ResponseEntity<?> marquerAlerteCommeLue(@PathVariable Long id) {
        candidatService.marquerCommeLue(id);
        return ResponseEntity.ok().build();
    }

    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal user) {
            return user.getUserId();
        }
        return null;
    }
    @DeleteMapping("/alertes/{userId}/{id}")
    public ResponseEntity<Void> removeFavori(@PathVariable Long id) {
        Long userId=extractUserIdFromToken();
        candidatService.deleteAlerte(userId, id);
        return ResponseEntity.noContent().build();
    }

}


