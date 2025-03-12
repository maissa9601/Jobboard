package com.example.authentication.Controller;

import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import java.util.Collections;
import com.example.authentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest) {
        if (authRequest == null || authRequest.getEmail() == null || authRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "requête invalide."));
        }
        try {

            String message = authService.register(authRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }



    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

  @GetMapping("/confirm")
  public ResponseEntity<Map<String, Boolean>> confirmAccount(@RequestParam("token") String token) {
      boolean isActivated = authService.confirmAccount(token);

      if (isActivated) {
          return ResponseEntity.ok(Map.of("activated", true));
      } else {
          return ResponseEntity.badRequest().body(Map.of("activated", false));
      }
  }


    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendResetPasswordEmail(request.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Un e-mail de réinitialisation a été envoyé."));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> googleLogin(OAuth2AuthenticationToken authToken) {
        OAuth2User user = authToken.getPrincipal();
        String jwt = authService.googleLogin(user);
        return ResponseEntity.ok("Authentifié avec Google.");
    }



    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification avec Google.");
    }



}
