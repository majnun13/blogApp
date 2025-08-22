package com.nurlan.service.interfaces;

import com.nurlan.dto.*;

public interface IAuthenticationService {

    public UserResponseDto register(RegisterRequestDto input);

    public AuthResponse login(LoginRequestDto input);

    public AuthResponse refreshToken(RefreshTokenRequest input);
}
