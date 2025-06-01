package com.example.candidat.Service;

import com.example.candidat.dto.CandidatMatchDto;
import com.example.candidat.dto.OffreDTO;
import com.example.candidat.dto.NotificationDto;
import com.example.candidat.dto.ReclamationEvent;
import com.example.candidat.model.Candidat;
import com.example.candidat.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlerteService {

    private final CandidateRepository candidatRepository;
    @Autowired
    private KafkaTemplate<String, ReclamationEvent> kafkaTemplate;
    private final RestTemplate restTemplate;

    private final String notificationBaseUrl = "http://localhost:8083/notifications";


    public List<CandidatMatchDto> findMatchingCandidatEmails(OffreDTO offre) {
        List<Candidat> candidats = candidatRepository.findAll();

        return candidats.stream()
                .filter(candidat -> isMatching(offre, candidat))
                .map(c -> new CandidatMatchDto(c.getUserId(), c.getEmail()))
                .collect(Collectors.toList());
    }


    private boolean isMatching(OffreDTO offre, Candidat candidat) {
        boolean matchLocation = candidat.getPreferredLocation() != null &&
                candidat.getPreferredLocation().equalsIgnoreCase(offre.getLocation());

        boolean matchContract = candidat.getPreferredContractType() != null &&
                candidat.getPreferredContractType().equalsIgnoreCase(offre.getContractType());

        boolean matchKeywords = false;
        if (candidat.getPreferredKeywords() != null && offre.getDescription() != null) {
            String descriptionLower = offre.getDescription().toLowerCase();
            matchKeywords = candidat.getPreferredKeywords().stream()
                    .anyMatch(keyword -> descriptionLower.contains(keyword.toLowerCase()));
        }

        return matchLocation && matchContract && matchKeywords;
    }

    public void sendReclamation(ReclamationEvent event) {
        kafkaTemplate.send("reclamation", event);
    }
    // Pour candidat
    public List<NotificationDto> getJobAlerts(String userId) {
        ResponseEntity<NotificationDto[]> response = restTemplate.getForEntity(
                notificationBaseUrl + "/job-alerts/" + userId,
                NotificationDto[].class
        );
        return Arrays.asList(response.getBody());
    }
}





