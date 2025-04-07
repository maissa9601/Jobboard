package com.example.admin.controller;

import com.example.admin.service.JobOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.admin.model.JobOffer;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class JobController {

    private final JobOfferService jobOfferService;


    public JobController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @PostMapping("/offer")
    public JobOffer createJobOffer(@RequestBody JobOffer jobOffer) {
        return jobOfferService.postJobOffer(jobOffer);
    }

    @GetMapping("/offers")
    public List<JobOffer> getAllJobOffer() {
        return jobOfferService.getAllJobOffer();
    }

    @GetMapping("/offer/{id}")
    public ResponseEntity<JobOffer> getJobOfferById(@PathVariable Long id) {
        return JobOfferService.getJobOfferById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/offers/{id}")
    public ResponseEntity<JobOffer> updateJobOffer(@PathVariable Long id, @RequestBody JobOffer jobOffer) {
        Optional<JobOffer> existingJobOfferOpt = JobOfferService.getJobOfferById(id);

        if (existingJobOfferOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        JobOffer existingJobOffer = existingJobOfferOpt.get();
        existingJobOffer.setCompany(jobOffer.getCompany());
        existingJobOffer.setDescription(jobOffer.getDescription());
        existingJobOffer.setTitle(jobOffer.getTitle());
        existingJobOffer.setLocation(jobOffer.getLocation());
        existingJobOffer.setSalary(jobOffer.getSalary());
        existingJobOffer.setSource(jobOffer.getSource());
        existingJobOffer.setContractype(jobOffer.getContractype());

        JobOffer updatedJobOffer = JobOfferService.updateJobOffer(existingJobOffer);
        return ResponseEntity.ok(updatedJobOffer);
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<Void> deleteJobOffer(@PathVariable Long id) {
        Optional<JobOffer> existingJobOfferOpt = JobOfferService.getJobOfferById(id);
        if (existingJobOfferOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JobOfferService.deleteJobOffer(id);
        return ResponseEntity.ok().build();
    }
}
