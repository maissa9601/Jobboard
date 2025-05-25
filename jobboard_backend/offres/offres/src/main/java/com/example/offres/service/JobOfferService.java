package com.example.offres.service;


import com.example.offres.model.Offer;
import com.example.offres.repository.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
    public class JobOfferService {
    @Autowired
    private JobOfferRepository jobOfferRepository;


    public List<Offer> getAllJobOffers() {
            return jobOfferRepository.findAll();
        }

    public Offer getJobById(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found"));
    }
    public Offer postJobOffer(Offer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }



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




}
