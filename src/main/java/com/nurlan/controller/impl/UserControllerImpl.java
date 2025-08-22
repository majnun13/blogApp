package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.IUserController;
import com.nurlan.dto.*;
import com.nurlan.service.interfaces.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements IUserController {

    @Autowired
    private IUserService userService;


    @Override
    @GetMapping("/me")
    public MyProfileDto getMyProfile(Authentication authentication,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "6") int size) {
        String authName = authentication.getName();
        return userService.getMyProfile(authName,page, size);
    }

    @Override
    @GetMapping("/{username}")
    public UserProfileDto getUserProfile(@PathVariable String username,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "6") int size) {
        return userService.getUserProfile(username, page, size);
    }

    @Override
    @PutMapping("/me")
    public MyProfileDto updateProfile(Authentication authentication, @RequestBody UpdateProfileRequestDto dto) {
        return userService.updateProfile(authentication.getName(), dto);
    }

    @Override
    @PutMapping("/me/change-password")
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        userService.changePassword(authentication.getName(), dto);
    }

    @Override
    @PostMapping("/me/avatar")
    public ImageUploadResponseDto uploadAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        return userService.uploadProfileImage(authentication.getName(), file);
    }
}
