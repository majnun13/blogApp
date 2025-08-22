package com.nurlan.service.interfaces;

import com.nurlan.dto.ResetPasswordRequestDto;
import com.nurlan.models.User;

public interface IEmailVerificationService {

    void sendVerificationEmail(User user);

    boolean verifyEmail(String token);

    void resendVerificationEmail(String email);

    void sendPasswordResetEmail(String email);

    boolean resetPassword(ResetPasswordRequestDto request);

    boolean validateResetToken(String token);
}