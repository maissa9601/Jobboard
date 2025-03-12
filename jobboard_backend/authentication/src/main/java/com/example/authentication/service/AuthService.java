package com.example.authentication.service;


import com.example.authentication.dto.AuthRequest;
import com.example.authentication.dto.AuthResponse;
import com.example.authentication.model.Role;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    //activation de compte
    public void sendActivationEmail(String email, String token) {
        String activationUrl = "http://localhost:4200/activate?token=" + token;

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


    public String register(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(Role.CANDIDAT);
        user.setEnabled(false);
        // token d'activation
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        userRepository.save(user);
        // mail d'activation
        sendActivationEmail(user.getEmail(), token);
        return "Un lien d'activation a été envoyé à votre adresse e-mail.";
    }
    public boolean confirmAccount(String token) {
        Optional<User> userOptional = userRepository.findByActivationToken(token);

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setActivationToken(null);
        userRepository.save(user);

        return true;
    }

    public AuthResponse login(AuthRequest request) {
        // existe?
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("utilisateur non trouvé"));

        // compte est activé?
        if (!user.isEnabled()) {
            throw new RuntimeException("compte non activé");
        }

        //  mot de passe est correct?
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        // générer un token JWT
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        String token = jwtUtil.generateToken(user.getEmail(), authorities);

        //token velide ou non
        if (jwtUtil.isTokenValid(token)) {
            return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
        } else {
            throw new RuntimeException("Token invalide !");
        }}

    public void sendResetPasswordEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            String token = UUID.randomUUID().toString();
            User user = userOptional.get();
            user.setResetToken(token);
            userRepository.save(user);

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
                throw new RuntimeException("error lors de l'envoi de l'email", e);
            }
        }
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Token invalide ou expiré.");
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