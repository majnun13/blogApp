package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.IAuthenticaionController;
import com.nurlan.dto.*;
import com.nurlan.service.interfaces.IAuthenticationService;
import com.nurlan.service.interfaces.IEmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController implements IAuthenticaionController {


    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IEmailVerificationService emailVerificationService;

   @PostMapping("/register")
    @Override
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto input) {
        return authenticationService.register(input);
    }

    @PostMapping("/login")
    @Override
    public AuthResponse login(@Valid @RequestBody LoginRequestDto input) {
        return authenticationService.login(input);
    }

    @PostMapping("/refresh-token")
    @Override
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        return authenticationService.refreshToken(input);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = emailVerificationService.verifyEmail(token);

            if (verified) {
                return ResponseEntity.ok(Map.of(
                        "message", "Email başarıyla doğrulandı! Artık giriş yapabilirsiniz.",
                        "success", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Geçersiz veya süresi dolmuş doğrulama linki",
                        "success", false
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email doğrulama sırasında hata oluştu: " + e.getMessage(),
                    "success", false
            ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            emailVerificationService.resendVerificationEmail(email);

            return ResponseEntity.ok(Map.of(
                    "message", "Doğrulama emaili yeniden gönderildi",
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "success", false
            ));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            emailVerificationService.sendPasswordResetEmail(email);

            return ResponseEntity.ok(Map.of(
                    "message", "Şifre sıfırlama linki email adresinize gönderildi",
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "success", false
            ));
        }
    }


    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean valid = emailVerificationService.validateResetToken(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        try {
            boolean reset = emailVerificationService.resetPassword(request);

            if (reset) {
                return ResponseEntity.ok(Map.of(
                        "message", "Şifreniz başarıyla güncellendi",
                        "success", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Geçersiz, kullanılmış veya süresi dolmuş sıfırlama linki",
                        "success", false
                ));
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", ex.getMessage(),
                    "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Şifre sıfırlama sırasında hata oluştu: " + e.getMessage(),
                    "success", false
            ));
        }

    }
}
