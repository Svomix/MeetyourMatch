package com.javanostra.spring.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;

    public void sendVerificationCodeEmail(String to, String subject, String token, String username) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            mimeMessage.setContent(buildVerificationCodeEmail(token, username), "text/html;charset=UTF-8");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }


    private String buildVerificationCodeEmail(String token, String username) {
        Path path = Paths.get("src/main/resources/static/verificationCodeEmail.html");
        Document doc = null;
        try {
            doc = Jsoup.parse(path);
            doc.getElementById("greetingUser").appendText("Здравствуйте, %s".formatted(username));
            doc.getElementById("code").appendText(token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc.html();
    }
}
