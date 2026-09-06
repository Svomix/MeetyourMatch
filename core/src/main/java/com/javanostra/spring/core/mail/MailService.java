package com.javanostra.spring.core.mail;

import com.javanostra.spring.core.entities.Token;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;

    @Async
    public void sendTokenInformationEmail(String to, String subject, Token token, String username) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildTokenInformationEmail(token.getToken(), username, subject), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private String buildTokenInformationEmail(String token, String username, String subject) {
        Document doc = null;
        try (InputStream is = new ClassPathResource("static/tokenInformationEmail.html").getInputStream()) {
            doc = Jsoup.parse(is, "UTF-8", "");
            doc.getElementById("greetingUser").appendText("Здравствуйте, %s".formatted(username));
            doc.getElementById("code").appendText(token);
            doc.getElementById("subject").appendText(subject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc != null ? doc.html() : "";
    }
}
