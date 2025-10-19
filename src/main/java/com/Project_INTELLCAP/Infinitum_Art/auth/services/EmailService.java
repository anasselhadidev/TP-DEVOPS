package com.Project_INTELLCAP.Infinitum_Art.auth.services;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static com.Project_INTELLCAP.Infinitum_Art.auth.utils.TemplateLoader.loadTemplate;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;
    @Value("${spring.mail.supportEmail}")
    private String supportEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(Users user, String token) {
        try {
            String to = user.getEmail();
            String subject = "Verify your account";
            String verifyUrl = frontendBaseUrl + "/api/auth/verify?verifyToken=" + token;

            Map<String, String> vars = Map.of(
                    "fullName", user.getUsername(),
                    "verifyUrl", verifyUrl,
                    "companyName", "Infinitum Art",
                    "supportEmail", supportEmail,
                    "privacyPolicyUrl", "#",
                    "termsOfServiceUrl", "#",
                    "unsubscribeUrl", "#"
            );

            String htmlContent = loadTemplate("email-verification.html", vars);
            sendEmail(to, subject, htmlContent);

        } catch (Exception e) {
            handleEmailException(e, "verification");
        }
    }

    public void sendResetEmail(Users user, String token) {
        try {
            String to = user.getEmail();
            String subject = "Reset your Password";
            String resetUrl = frontendBaseUrl + "/api/auth/reset-password?token=" + token;

            Map<String, String> vars = Map.of(
                    "fullName", user.getUsername(),
                    "resetUrl", resetUrl,
                    "companyName", "Infinitum Art",
                    "supportEmail", supportEmail,
                    "privacyPolicyUrl", "#",
                    "termsOfServiceUrl", "#",
                    "unsubscribeUrl", "#"
            );

            String htmlContent = loadTemplate("email-reset-password.html", vars);
            sendEmail(to, subject, htmlContent);

        } catch (Exception e) {
            handleEmailException(e, "password reset");
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    private void handleEmailException(Exception e, String emailType) {
        if (e instanceof MailAuthenticationException) {
            throw new IllegalStateException("Email service authentication failed. Please contact support.");
        } else if (e instanceof MailSendException) {
            throw new IllegalStateException("Unable to send " + emailType + " email. Please try again later.");
        } else if (e instanceof MessagingException) {
            throw new IllegalStateException("Failed to compose " + emailType + " email due to configuration issues.");
        } else {
            throw new RuntimeException("Failed to send " + emailType + " email due to an unexpected error", e);
        }
    }

    public void sendEmailChangeVerification(Users user, String token, String toEmail, String subject) {
        // Utiliser le même endpoint que le register pour être cohérent
        String verifyUrl = "http://localhost:8080/api/auth/verify?verifyToken=" + token;

        Map<String, String> vars = Map.of(
                "fullName", user.getUsername(),
                "verifyUrl", verifyUrl,
                "companyName", "Infinitum Art",
                "supportEmail", supportEmail,
                "privacyPolicyUrl", "#",
                "termsOfServiceUrl", "#",
                "unsubscribeUrl", "#"
        );
        String htmlContent = loadTemplate("email-verification.html", vars);


        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML flag = true

            mailSender.send(mimeMessage);
        } catch (MailAuthenticationException e) {
            // SMTP credentials issue (e.g. bad username/password)
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Email service authentication failed. Contact support."
            );
        } catch (MailSendException e) {
            // General sending failure (e.g. bad address format, unreachable host)
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to send verification email. Please try again later."
            );
        } catch (MailException e) {
            // Catch-all for other mail-related issues
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error while sending email"
            );
        } catch (Exception e) {
            // Absolute fallback
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error occurred during email dispatch"
            );
        }
    }
}