package com.example.offres.controller;

import com.example.offres.model.Offer;
import com.example.offres.service.JobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("")
@CrossOrigin(origins = "http://localhost:4200")
public class JobOfferController {

    @Autowired
    private JobOfferService jobOfferService;


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


    @DeleteMapping("/offer/{id}")
    public ResponseEntity<Void> deleteJobOffer(@PathVariable Long id) {
        boolean deleted = jobOfferService.deleteJobOffer(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }






}
