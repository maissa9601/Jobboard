package com.example.candidat.Service;

import com.example.candidat.dto.OffreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAlertEmail(String to, OffreDTO offre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🚀 New Job Opportunity Just for You!");

        String content = "Hello,\n\n"
                + "We've found a job opportunity that matches your preferences! 🎯\n\n"
                + "📌 *Job Title*: " + offre.getTitle() + "\n"
                + "📝 *Description*: " + offre.getDescription() + "\n"
                + "🔗 *Apply here*: " + offre.getOfferUrl() + "\n\n"
                + "Don't miss out—check it out now!\n\n"
                + "Best regards,\n"
                + "Your Job Alert Team";

        message.setText(content);
        mailSender.send(message);
    }

}
