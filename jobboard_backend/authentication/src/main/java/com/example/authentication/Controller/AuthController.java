package com.example.authentication.Controller;

import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import com.example.authentication.model.User;

import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;




@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        if (authRequest == null) {
            return ResponseEntity.badRequest().body("Requête invalide.");
        }

        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(authRequest.getPassword());

        String responseMessage = authService.register(user);
        return ResponseEntity.ok(responseMessage);
    }




    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmAccount(@RequestParam String token) {
        String response = authService.confirmAccount(token);
        if (response.equals("Votre compte est activé !")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    /*@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(token, newPassword));
    }*/
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendResetPasswordEmail(request.getEmail());
        return ResponseEntity.ok("Un e-mail de réinitialisation a été envoyé.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request.getToken(), request.getNewPassword()));
    }
    @GetMapping("/oauth2/success")
    public ResponseEntity<String> googleLogin(OAuth2AuthenticationToken authToken) {
        OAuth2User user = authToken.getPrincipal();
        String jwt = authService.googleLogin(user);
        return ResponseEntity.ok("Authentifié avec Google. Token JWT : " + jwt);
    }



    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification avec Google.");
    }



}
