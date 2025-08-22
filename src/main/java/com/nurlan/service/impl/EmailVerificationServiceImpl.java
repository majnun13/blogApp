package com.nurlan.service.impl;

import com.nurlan.dto.ResetPasswordRequestDto;
import com.nurlan.enums.TokenType;
import com.nurlan.models.EmailVerificationToken;
import com.nurlan.models.User;
import com.nurlan.repository.EmailVerificationRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.IEmailService;
import com.nurlan.service.interfaces.IEmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements IEmailVerificationService {


    @Autowired
    private final EmailVerificationRepository tokenRepository;

    @Autowired
    private final IEmailService emailService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.mail.verification.token-expiry-minutes:15}")
    private int tokenExpiryMinutes;

    private static final SecureRandom secureRandom = new SecureRandom();

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }



    @Override
    @Transactional
    public void sendVerificationEmail(User user) {
        tokenRepository.deleteByUserAndTokenType(user, TokenType.EMAIL_VERIFICATION);

        String token = generateSecureToken();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenType(TokenType.EMAIL_VERIFICATION);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));
        verificationToken.setCreatedAt(LocalDateTime.now()); // Bu satırı ekle

        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);

        log.info("Verification email sent to user: {}", user.getEmail());

    }

    @Override
    @Transactional
    public boolean verifyEmail(String tokenValue) {
        EmailVerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElse(null);

        if (token == null || token.isExpired() || token.isVerified()) {
            log.warn("Invalid, expired or already verified token: {}", tokenValue);
            return false;
        }

        // Token'ı doğrulandı olarak işaretle
        token.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(token);

        // User'ı güncelle
        User user = token.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);

        // Hoş geldin emaili gönder
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        log.info("Email verified successfully for user: {}", user.getEmail());
        return true;

    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email zaten doğrulanmış");
        }

        sendVerificationEmail(user);

    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu email adresi ile kayıtlı kullanıcı bulunamadı"));

        // Mevcut şifre sıfırlama tokenlarını sil
        tokenRepository.deleteByUserAndTokenType(user, TokenType.PASSWORD_RESET);

        String token = generateSecureToken();

        EmailVerificationToken resetToken = new EmailVerificationToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setTokenType(TokenType.PASSWORD_RESET);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30)); // 30 dakika
        resetToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset email sent to user: {}", user.getEmail());

    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordRequestDto request) {
        // 1) confirmPassword kontrolü
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Şifre ve tekrar eşleşmiyor");
        }

        EmailVerificationToken token = tokenRepository.findByToken(request.getToken()).orElse(null);
        if (token == null
                || token.getTokenType() != TokenType.PASSWORD_RESET
                || token.isExpired()
                || token.getVerifiedAt() != null) {
            log.warn("Invalid, expired or already used password reset token: {}", request.getToken());
            return false;
        }

        User user = token.getUser();

        // Eski şifre ile aynı mı?
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Yeni şifre eski şifreyle aynı olamaz");
        }

        // Şifreyi güncelle
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Token'ı tüket
        token.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(token);
        tokenRepository.delete(token); // ekstra güvenlik

        log.info("Password reset successfully for user: {}", user.getEmail());
        return true;
    }

    @Override
    public boolean validateResetToken(String tokenValue) {
        EmailVerificationToken token = tokenRepository.findByToken(tokenValue).orElse(null);
        if (token == null) return false;
        if (token.getTokenType() != TokenType.PASSWORD_RESET) return false;
        if (token.isExpired()) return false;
        if (token.getVerifiedAt() != null) return false; // zaten kullanılmış
        return true;
    }

}
