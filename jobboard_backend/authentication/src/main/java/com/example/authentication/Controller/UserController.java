package com.example.authentication.Controller;

import com.example.authentication.dto.Candidat;
import com.example.authentication.model.User;
import com.example.authentication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User> >getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }

    @GetMapping("/candidats")
    public ResponseEntity<List<User>> getAllCandidats() {
        return ResponseEntity.ok(userService.getAllCandidats());
    }

    @PostMapping("/candidats/{id}/promote")
    public ResponseEntity<User> promoteToAdmin(@PathVariable Long id) {
        return userService.promoteToAdmin(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/candidats/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
        Optional<User> existing = userService.getCandidatById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteCandidat(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/candidats/{id}")
    public Optional<User> getCandidatById(@PathVariable Long id) {
       return  userService.getCandidatById(id);
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("candidats", userService.countCandidats());
        stats.put("admins", userService.countAdmins());
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/candidats/recent")
    public ResponseEntity<List<Candidat>> getRecentCandidats() {
        return ResponseEntity.ok(userService.getRecentCandidats());
    }

}
