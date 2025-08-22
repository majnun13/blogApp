package com.nurlan.controller.interfaces;

import com.nurlan.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface IUserController {

    public MyProfileDto getMyProfile(Authentication authentication, int page, int size);
    public UserProfileDto getUserProfile(String username, int page, int size);
    public MyProfileDto updateProfile(Authentication authentication, UpdateProfileRequestDto dto);
    public void changePassword(Authentication authentication, ChangePasswordRequestDto dto);
    public ImageUploadResponseDto uploadAvatar(Authentication authentication, MultipartFile file);

}
