package com.nurlan.controller.interfaces;

import com.nurlan.dto.*;

public interface IAuthenticaionController {

    public UserResponseDto register(RegisterRequestDto input);

    public AuthResponse login(LoginRequestDto input);

    public AuthResponse refreshToken(RefreshTokenRequest input);
}
