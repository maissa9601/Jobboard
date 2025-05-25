package com.example.offres.controller;


import com.example.offres.model.Offer;
import com.example.offres.Dto.OffreDTO;
import com.example.offres.service.JobOfferService;
import com.example.offres.repository.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("")
@CrossOrigin(origins = "http://localhost:4200")
public class JobOfferController {

    @Autowired
    private JobOfferService jobOfferService;
    @Autowired
    private JobOfferRepository offerRepository;
    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/offers")
    public List<Offer> getAllJobOffers() {
        return jobOfferService.getAllJobOffers();
    }
    @GetMapping("/offer/{id}")
    public ResponseEntity<Offer> getJobById(@PathVariable Long id) {
        Offer jobOffer = jobOfferService.getJobById(id);
        return ResponseEntity.ok(jobOffer);
    }
    @PostMapping("/offer")
    public Offer createJobOffer(@RequestBody Offer jobOffer) {
        return jobOfferService.postJobOffer(jobOffer);
    }



    @PutMapping("/offer/{id}")
    public ResponseEntity<Offer> updateJobOffer(@PathVariable Long id, @RequestBody Offer jobOffer) {
        Offer updatedJobOffer = jobOfferService.updateJobOffer(id, jobOffer);
        return updatedJobOffer != null ? ResponseEntity.ok(updatedJobOffer) : ResponseEntity.notFound().build();
    }


    @PostMapping("/offer/add")
    public ResponseEntity<Offer> ajouterOffre(@RequestBody Offer offre) {
        Offer saved = offerRepository.save(offre);

        OffreDTO dto = new OffreDTO(saved.getTitle(), saved.getDescription(), saved.getLocation(), saved.getContractype(), saved.getUrl());
        restTemplate.postForObject("http://localhost:8083/candidat/new", dto, Void.class);

        return ResponseEntity.ok(saved);
    }



    @DeleteMapping("/offer/{id}")
    public ResponseEntity<Void> deleteJobOffer(@PathVariable Long id) {
        boolean deleted = jobOfferService.deleteJobOffer(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/offers/{id}/increment")
    public ResponseEntity<Offer> seeMore(@PathVariable Long id) {
        Optional<Offer> optionalOffre = offerRepository.findById(id);

        if (optionalOffre.isPresent()) {
            Offer offre = optionalOffre.get();
            offre.incrementViews();
            offerRepository.save(offre);

            return ResponseEntity.ok(offre);
        }

        return ResponseEntity.notFound().build();
    }
    @GetMapping("/offers/most-viewed")
    public List<Offer> getMostViewedOffers() {
        return offerRepository.findTop8ByOrderByViewsDesc();
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("offers", jobOfferService.countoffers());
        return ResponseEntity.ok(stats);
    }


}
