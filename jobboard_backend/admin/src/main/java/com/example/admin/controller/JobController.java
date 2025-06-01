package com.example.admin.controller;

import com.example.admin.Dto.JobOffer;
import com.example.admin.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/offers")
    public ResponseEntity<List<JobOffer>> getAllOffers() {
        return ResponseEntity.ok(jobService.getAllOffers());
    }

    @GetMapping("/offer/{id}")
    public ResponseEntity<JobOffer> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getOfferById(id));
    }

    @PostMapping("/offer/add")
    public ResponseEntity<JobOffer> createJobOffer(@RequestBody JobOffer jobOffer) {
        return ResponseEntity.ok(jobService.createJobOffer(jobOffer));
    }

    @PutMapping("/offer/{id}")
    public ResponseEntity<Void> updateOffer(@PathVariable Long id, @RequestBody JobOffer offer) {
        jobService.updateOffer(id, offer);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/offer/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        jobService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/offers/most-viewed")
    public ResponseEntity<List<JobOffer>> mostViewedOffers() {
        return ResponseEntity.ok(jobService.getMostViewedOffers());
    }

    @GetMapping("/offers/stats")
    public ResponseEntity<Map<String, Long>> getOfferStats() {
        return ResponseEntity.ok(jobService.getOfferStats());
    }
}
