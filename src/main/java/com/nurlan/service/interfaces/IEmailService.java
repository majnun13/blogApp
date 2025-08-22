package com.nurlan.service.interfaces;

public interface IEmailService {

    void sendVerificationEmail(String toEmail, String verificationToken);

    void sendPasswordResetEmail(String toEmail, String resetToken);

    void sendWelcomeEmail(String toEmail, String firstName);
}