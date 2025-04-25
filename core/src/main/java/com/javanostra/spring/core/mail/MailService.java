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

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;

    @Async
    public void sendTokenInformationEmail(String to, String subject, Token token, String username) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            mimeMessage.setContent(buildTokenInformationEmail(token.getToken(), username, subject), "text/html;charset=UTF-8");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }


    private String buildTokenInformationEmail(String token, String username, String subject) {
        Document doc = null;
        try {
            File file = new ClassPathResource("static/tokenInformationEmail.html").getFile();
            doc = Jsoup.parse(file);
            doc.getElementById("greetingUser").appendText("Здравствуйте, %s".formatted(username));
            doc.getElementById("code").appendText(token);
            doc.getElementById("subject").appendText(subject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc.html();
    }
}
