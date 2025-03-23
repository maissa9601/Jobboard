package com.example.authentication.service;


import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.SignatureException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;


@Service
public class AuthService {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${jwt.secret}")
    private String secretKey;

    //activation de compte
    public void sendActivationEmail(String email) {
        // token jwt
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 heures
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println("token: " + token);


        String activationUrl = "http://localhost:4200/activate?token=" + token;



        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🔐 Activate Your Account - JobBoard");

            // HTML content
            String emailContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; max-width: 500px; margin: auto;'>" +
                    "<h2 style='color: #007bff; text-align: center;'>Welcome to JobBoard!</h2>" +
                    "<p>Hello,</p>" +
                    "<p>Thank you for signing up. Click the button below to activate your account:</p>" +
                    "<p style='text-align: center;'>" +
                    "<a href='" + activationUrl + "' style='background-color: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>🔓 Activate My Account</a>" +
                    "</p>" +
                    "<p>If you did not request this registration, simply ignore this email.</p>" +
                    "<br/>" +
                    "<p style='text-align: center; color: #555;'>Best regards,<br><strong>The JobBoard Team</strong></p>" +
                    "</div>";

            helper.setText(emailContent, true);
            mailSender.send(message);

            System.out.println("email sent to: " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("error sending email.");
        }
    }


    public String register(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("this email already exists.");
        }
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(Role.CANDIDAT);
        user.setEnabled(false);
        userRepository.save(user);
        // mail d'activation
        sendActivationEmail(user.getEmail());

        return " A link was successfully sent to your email";
    }
    public boolean confirmAccount(String token) {
        try {
            // vérifier et décoder
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            String email = claims.getSubject();

            // existe?
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return false;
            }

            User user = userOptional.get();
            if (user.isEnabled()) {
                return false;
            }

            user.setEnabled(true);
            userRepository.save(user);

            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public AuthResponse login(AuthRequest request) {
        // existe?
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("user not found"));

        // compte est activé?
        if (!user.isEnabled()) {
            throw new RuntimeException("account is disabled.");
        }

        //  mot de passe est correct?
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("incorrect password.");
        }
        // générer un token JWT
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        String token = jwtUtil.generateToken(user.getEmail(), authorities);

        //token velide ou non
        if (jwtUtil.isTokenValid(token)) {
            return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
        } else {
            throw new RuntimeException("invalid token.!");
        }}

    public void sendResetPasswordEmail(String email) {

            String token = Jwts.builder()
                    .setSubject(email)
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 heure
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
            System.out.println("token: " + token);
            // link
            String resetUrl = "http://localhost:4200/reset?token=" + token;

            // html
            String emailContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; max-width: 500px; margin: auto;'>" +
                    "<h2 style='color: #4CAF50;'>Password Reset Request</h2>" +
                    "<p>Hello,</p>" +
                    "<p>We received a request to reset your password. If you did not request this, please ignore this email.</p>" +
                    "<p>Click the button below to reset your password:</p>" +
                    "<p style='text-align: center;'>" +
                    "<a href='" + resetUrl + "' style='background-color: #4CAF50; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                    "</p>" +
                    "<p>This link is valid for a limited time.</p>" +
                    "<p>Thank you,<br>JobBoard Team</p>" +
                    "</div>";

            // creation
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(email);
                helper.setSubject("🔑 Reset Your Password");
                helper.setText(emailContent, true);//enable html

                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                throw new RuntimeException("error while sending the mail", e);
            }

    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        try {
            // Décoder et vérifier le JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("Invalid email.");
            }

            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return "Password reset successfully";

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Expired token.");
        }
    }

    public String googleLogin(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();

        } else {

            user = new User();
            user.setEmail(email);
            user.setRole(Role.CANDIDAT);
            user.setEnabled(true);
            user.setPassword(UUID.randomUUID().toString());
            user = userRepository.save(user);

        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

         return "http://localhost:4200/callback?token=" + jwtUtil.generateToken(email, authorities);
    }

}