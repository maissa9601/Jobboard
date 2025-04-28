package com.example.authentication.Controller;

import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import java.io.IOException;
import java.util.Collections;
import com.example.authentication.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
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
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "invalid query."));
        }
        try {

            String message = authService.register(authRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
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
        return ResponseEntity.ok(Collections.singletonMap("message", "An email has been sent to your account."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }


    @GetMapping("/google")
    public void googleLogin(OAuth2AuthenticationToken authToken, HttpServletResponse response) throws IOException {
        OAuth2User user = authToken.getPrincipal();

        response.sendRedirect(authService.googleLogin(user));
    }


    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail to authenticate with google.");
    }







}
