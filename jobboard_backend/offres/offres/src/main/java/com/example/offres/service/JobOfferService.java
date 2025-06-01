package com.example.offres.service;


import com.example.offres.Dto.CandidatMatchDto;
import com.example.offres.Dto.JobAlertEvent;

import com.example.offres.model.Offer;
import com.example.offres.repository.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
    public class JobOfferService {
    @Autowired
    private JobOfferRepository jobOfferRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String, JobAlertEvent> kafkaTemplate;



    public List<Offer> getAllJobOffers() {
            return jobOfferRepository.findAll();
        }

    public Offer getJobById(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found"));
    }
    /*public Offer postJobOffer(Offer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }*/



    public Offer updateJobOffer(Long id, Offer jobOffer) {
        Optional<Offer> existingJobOfferOpt = jobOfferRepository.findById(id);

        if (existingJobOfferOpt.isEmpty()) {
            return null;
        }

        Offer existingJobOffer = existingJobOfferOpt.get();
        existingJobOffer.setCompany(jobOffer.getCompany());
        existingJobOffer.setDescription(jobOffer.getDescription());
        existingJobOffer.setTitle(jobOffer.getTitle());
        existingJobOffer.setLocation(jobOffer.getLocation());
        existingJobOffer.setSalary(jobOffer.getSalary());

        return jobOfferRepository.save(existingJobOffer);
    }

    public boolean deleteJobOffer(Long id) {
        if (!jobOfferRepository.existsById(id)) {
            return false;
        }
        jobOfferRepository.deleteById(id);
        return true;
    }
    public long countoffers() {
        return jobOfferRepository.count();
    }

    //alerte
    public Offer createAndNotify(Offer offre) {
        // Sauvegarder l'offre
        Offer saved = jobOfferRepository.save(offre);

        // Appeler candidat pour matcher
        ResponseEntity<List<CandidatMatchDto>> response = restTemplate.exchange(
                "http://localhost:8083/candidats/match-candidates",
                HttpMethod.POST,
                new HttpEntity<>(saved),
                new ParameterizedTypeReference<List<CandidatMatchDto>>() {}
        );

        List<CandidatMatchDto> matchedCandidats = response.getBody();

        //envoyer a kafka
        if (matchedCandidats != null) {
            matchedCandidats.forEach(candidat -> {
                JobAlertEvent event = new JobAlertEvent(
                        candidat.getId(),
                        candidat.getEmail(),
                        saved.getTitle(),
                        saved.getDescription()
                );
                kafkaTemplate.send("new-job-alerts", event);
            });
        }

        return saved;
    }
    }
