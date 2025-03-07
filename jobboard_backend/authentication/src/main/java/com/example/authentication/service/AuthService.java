package com.example.authentication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.reactive.TransactionalOperator;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;


    /**
     * Sign Up
     */
    /*public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        //role candidat
        if (user.getRole() == null) {
            user.setRole(Role.CANDIDAT);
        }

        // hachage
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }*/
    /*private void sendActivationEmail(String email, String token) {
        String activationUrl = "http://localhost:8080/auth/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmez votre inscription");
        message.setText("Cliquez sur le lien pour activer votre compte : " + activationUrl);

        mailSender.send(message);
    }
    */


    public void sendActivationEmail(String email, String token) {
        String activationUrl = "http://localhost:8080/auth/confirm?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🔐 Activez votre compte - JobBoard");

            // html
            String htmlContent = """
                    <html>
                        <body>
                            <h2>Welcome to JobBoard!</h2>
                            <p>Thank you for signing up. Click the button below to activate your account:</p>
                            <p style="text-align: center;">
                                <a href="%s" style="display: inline-block; padding: 10px 20px; font-size: 16px;\s
                                    color: white; background-color: #007bff; text-decoration: none; border-radius: 5px;">
                                    🔓 Activate My Account
                                </a>
                            </p>
                            <p>If you did not request this registration, simply ignore this email.</p>
                            <br/>
                            <p>Best regards, <br/> <strong>The JobBoard Team</strong></p>
                        </body>
                    </html>
                
                """.formatted(activationUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("📩 Mail envoyé à : " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail.");
        }
    }

    public String register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        user.setRole(Role.CANDIDAT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // désactivé tant qu'il n'a pas confirmé

        //  générer un token d'activation
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        userRepository.save(user);

        //envoi de mail
        sendActivationEmail(user.getEmail(), token);

        return " Un lien d'activation a été envoyé à votre adresse e-mail.";
    }



    public String confirmAccount(String token) {
        Optional<User> userOptional = userRepository.findByActivationToken(token);

        if (userOptional.isEmpty()) {
            return "Lien d'activation invalide ou expiré.";
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setActivationToken(null);
        userRepository.save(user);

        return "Votre compte est activé !";
    }

    /**
     *  Sign In
     */
    /*public AuthResponse login(AuthRequest request) {
        // existe ou non
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // mdp correspond
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        //générer token
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        String token = jwtUtil.generateToken(user.getEmail(), authorities);

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
    }*/
    public AuthResponse login(AuthRequest request) {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // compte est activé?
        if (!user.isEnabled()) {
            throw new RuntimeException("Compte non activé. Veuillez activer votre compte.");
        }

        //  mot de passe est correct?
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // générer un token JWT
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        String token = jwtUtil.generateToken(user.getEmail(), authorities);

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
    }

    public void sendResetPasswordEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            String token = UUID.randomUUID().toString();
            User user = userOptional.get();
            user.setResetToken(token);
            userRepository.save(user);

            String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Réinitialisation de votre mot de passe");
            message.setText("Cliquez ici pour réinitialiser votre mot de passe : " + resetUrl);

            mailSender.send(message);
        }
    }
    @Transactional
    public String resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            return "Token invalide ou expiré.";
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return "Mot de passe mis à jour !";
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
        return jwtUtil.generateToken(email, authorities);
    }

}