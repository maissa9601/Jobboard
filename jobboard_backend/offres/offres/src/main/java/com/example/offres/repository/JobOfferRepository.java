package com.example.offres.repository;



import com.example.offres.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface JobOfferRepository extends JpaRepository<Offer, Long> {



}
