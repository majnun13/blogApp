package com.nurlan.service.interfaces;

import com.nurlan.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    public UserProfileDto getUserProfile(String username, int page, int size);
    public MyProfileDto getMyProfile(String authUsername, int page, int size);
    public MyProfileDto updateProfile(String authUsername, UpdateProfileRequestDto input);
    public ImageUploadResponseDto uploadProfileImage(String authUsername, MultipartFile file);
    void changePassword(String authUsername, ChangePasswordRequestDto input);
}
