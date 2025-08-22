package com.nurlan.service.impl;

import com.nurlan.dto.*;
import com.nurlan.enums.Role;
import com.nurlan.exception.*;
import com.nurlan.jwt.JwtService;
import com.nurlan.models.RefreshToken;
import com.nurlan.models.User;
import com.nurlan.repository.RefreshTokenRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.IAuthenticationService;
import com.nurlan.service.interfaces.IEmailVerificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IEmailVerificationService emailVerificationService;


    private User createUser(RegisterRequestDto input){
        User user = new User();
        user.setUsername(input.getUsername());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setCreatedDate(new Date());
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        return user;
    }


    private void validateUserUniqueness(RegisterRequestDto input){
        boolean usernameExists = userRepository.existsByUsername(input.getUsername());
        boolean emailExists = userRepository.existsByEmail(input.getEmail());
        if(usernameExists && emailExists){
            throw new DuplicateResourceException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS, input.getUsername() + " : " + input.getEmail() ));
        } else if (usernameExists) {
            throw new DuplicateResourceException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, input.getUsername()));
        }else if (emailExists) {
            throw new DuplicateResourceException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, input.getEmail()));
        }
    }

    private void validatePasswordsMatch(RegisterRequestDto input){
        if(!input.getPassword().equals(input.getConfirmPassword())){
            throw new BaseException(new ErrorMessage(MessageType.CONFIRM_PASSWORD_ERROR, null));
        }
    }

    private RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreatedDate(new Date());
        refreshToken.setExpireDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8));
        refreshToken.setUser(user);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        return refreshToken;
    }

    @Override
    public UserResponseDto register(RegisterRequestDto input) {
        validateUserUniqueness(input);
        validatePasswordsMatch(input);

        User savedUser = userRepository.save(createUser(input));

        try {
            emailVerificationService.sendVerificationEmail(savedUser);
        } catch (Exception e) {
            // Eğer email gönderiminde hata olursa kullanıcıyı sil
            userRepository.delete(savedUser);
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Doğrulama emaili gönderilemedi"));
        }
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(savedUser, userResponseDto);

        return userResponseDto;
    }

    @Override
    public AuthResponse login(LoginRequestDto input) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            User user = userRepository.findByUsername(input.getUsername())
                    .orElseThrow(() -> new BaseException(
                            new ErrorMessage(MessageType.USERNAME_NOT_FOUND, input.getUsername())));

            if (!user.isEmailVerified() || !user.isEnabled()) {
                throw new BaseException(
                        new ErrorMessage(MessageType.EMAIL_MUST_BE_VERIFICATED, user.getEmail()));
            }

            String accessToken = jwtService.generateToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            Role role = user.getRole();

            AuthorDto authorDto = new AuthorDto().builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .avatarUrl(user.getImageUrl())
                    .role(user.getRole())
                    .build();
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(savedRefreshToken.getRefreshToken())
                    .author(authorDto)
                    .build();

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // 401
            throw new UnauthorizedException(
                    new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, null));
        } catch (UnauthorizedException | ForbiddenException e) {
            throw e; // status korunsun
        } catch (Exception e) {
            // kalan her şey 500 görmek istersen ayrı handler ekleyebilirsin; şimdilik 400 kalabilir
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, e.getMessage()));
        }
    }

    private boolean isRefreshTokenValid(Date expireDate){
        return new Date().before(expireDate);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest input) {
        Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByRefreshToken(input.getRefreshToken());
        if(optRefreshToken.isPresent()){
            if(!isRefreshTokenValid(optRefreshToken.get().getExpireDate())){
                throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_IS_EXPIRED, input.getRefreshToken()));
            }
            User user = optRefreshToken.get().getUser();
            String accessToken = jwtService.generateToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            AuthorDto authorDto = new AuthorDto().builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .avatarUrl(user.getImageUrl())
                    .role(user.getRole())
                    .build();


            return AuthResponse.builder()
                    .refreshToken(savedRefreshToken.getRefreshToken())
                    .accessToken(accessToken)
                    .author(authorDto)
                    .build();

        }
        else {
            throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, input.getRefreshToken()));
        }
    }

}
