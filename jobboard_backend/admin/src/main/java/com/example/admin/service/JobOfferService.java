package com.example.admin.service;

import com.example.admin.model.JobOffer;
import com.example.admin.repository.JobOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobOfferService {
    private static JobOfferRepository jobOfferRepository;

    public JobOfferService(JobOfferRepository jobOfferRepository) {
        JobOfferService.jobOfferRepository = jobOfferRepository;
    }

    public JobOffer postJobOffer(JobOffer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }

    public List<JobOffer> getAllJobOffer() {
        return jobOfferRepository.findAll();
    }
    public static Optional<JobOffer> getJobOfferById(Long id) {
        return jobOfferRepository.findById(id);
    }
    public static JobOffer updateJobOffer(JobOffer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }
    public static void deleteJobOffer(Long id) {
        jobOfferRepository.deleteById(id);
    }
}
