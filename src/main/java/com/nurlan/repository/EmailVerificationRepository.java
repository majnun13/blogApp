package com.nurlan.repository;

import com.nurlan.enums.TokenType;
import com.nurlan.models.EmailVerificationToken;
import com.nurlan.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository  extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUserAndTokenType(User user, TokenType tokenType);


}
