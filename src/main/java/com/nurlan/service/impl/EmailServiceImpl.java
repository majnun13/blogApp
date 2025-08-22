package com.nurlan.service.impl;
import com.nurlan.service.interfaces.IEmailService;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;


    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress); // sabit string yerine config
            message.setTo(toEmail);
            message.setSubject("BlogApp - Email Adresinizi Doğrulayın");

            // ✅ FE rotası
            String verificationLink = frontendBaseUrl + "/verify-email?token=" + verificationToken;

            String content = """
                Merhaba,

                BlogApp'e hoş geldiniz! Hesabınızı aktifleştirmek için email adresinizi doğrulamanız gerekmektedir.

                Aşağıdaki linke tıklayarak email adresinizi doğrulayabilirsiniz:
                %s

                Bu link 15 dakika süreyle geçerlidir.

                Eğer bu hesabı siz oluşturmadıysanız, bu emaili görmezden gelebilirsiniz.

                BlogApp Ekibi
                """.formatted(verificationLink);

            message.setText(content);
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Email gönderilemedi", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("BlogApp - Şifre Sıfırlama");

            // ✅ FE rotası
            String resetLink = frontendBaseUrl + "/reset-password?token=" + resetToken;

            String content = """
                Merhaba,

                BlogApp hesabınız için şifre sıfırlama talebinde bulundunuz.

                Aşağıdaki linke tıklayarak yeni şifrenizi belirleyebilirsiniz:
                %s

                Bu link 30 dakika süreyle geçerlidir.

                Eğer bu talebi siz yapmadıysanız, bu emaili görmezden gelin. Hesabınız güvende kalacaktır.

                BlogApp Ekibi
                """.formatted(resetLink);

            message.setText(content);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Şifre sıfırlama emaili gönderilemedi", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("BlogApp'e Hoş Geldiniz!");

            String content = """
                Merhaba %s,
                
                BlogApp'e hoş geldiniz! Email adresinizi başarıyla doğruladınız.
                
                Artık BlogApp'te:
                - Blog yazıları okuyabilir
                - Kendi blog yazılarınızı paylaşabilir  
                - Diğer yazarlara yorum yapabilirsiniz
                
                Keyifli okumalar!
                
                BlogApp Ekibi
                """.formatted(firstName);

            message.setText(content);
            mailSender.send(message);

            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }


}
