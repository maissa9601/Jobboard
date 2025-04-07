package com.example.admin.controller;

import com.example.admin.model.Candidat;
import com.example.admin.service.CandidatService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class CandidatController {
    @GetMapping("/candidats")
    public List<Candidat> getAllCandidats() {
        return CandidatService.getAllCandidats();
    }
}
