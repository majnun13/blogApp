package com.nurlan.service.impl;

import com.nurlan.dto.*;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Post;
import com.nurlan.models.User;
import com.nurlan.repository.PostRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // UserServiceImpl içinde
    private static final long MIN_SIZE = 350L * 1024;        // 350 KB
    private static final long MAX_SIZE = 10L * 1024 * 1024;  // 10 MB


    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));


        return toUserProfileDto(user, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MyProfileDto getMyProfile(String authUsername, int page, int size) {
        User user = userRepository.findByUsername(authUsername)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, authUsername)));

        return toMyProfileDto(user, page, size);
    }

    @Override
    public MyProfileDto updateProfile(String authUsername, UpdateProfileRequestDto input) {

        User user = userRepository.findByUsername(authUsername)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, authUsername)));

        if (!passwordEncoder.matches(input.getCurrentPassword(), user.getPassword())) {
            throw new BaseException(new ErrorMessage(
                    MessageType.PASSWORD_IS_WRONG, input.getCurrentPassword()));
        }

        if(input.getUsername() != null && !input.getUsername().equals(user.getUsername())){
            boolean exists = userRepository.existsByUsername(input.getUsername());
            if(exists){
                throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, input.getUsername()));
            }
            user.setUsername(input.getUsername());
        }

        if(input.getFirstName() != null){
            user.setFirstName(input.getFirstName());
        }
        if(input.getLastName() != null){
            user.setLastName(input.getLastName());
        }
        if(input.getBirthOfDate() != null){
            user.setBirthOfDate(input.getBirthOfDate());
        }

        userRepository.save(user);
        return toMyProfileDto(user, 0, 6);
    }

    @Override
    public ImageUploadResponseDto uploadProfileImage(String authUsername, MultipartFile file) {

        User user = userRepository.findByUsername(authUsername)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, authUsername)));

        if (file.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.IMAGE_FILE_IS_EMPTY, file.getOriginalFilename()));
        }

        long size = file.getSize();
        if (size < MIN_SIZE) {
            throw new BaseException(new ErrorMessage(MessageType.FILE_MUST_BE_BIGGER_THAN_350KBPS, file.getOriginalFilename()));
        }
        if (size > MAX_SIZE) {
            throw new BaseException(new ErrorMessage(MessageType.FILE_MUST_BE_SMALLER_THAN_10_MB, file.getOriginalFilename()));
        }
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new BaseException(new ErrorMessage(MessageType.IMAGE_FILE_NAME_NOT_FOUND,""));
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
                throw new BaseException(new ErrorMessage(
                        MessageType.FILE_MUST_BE_JPG_OR_PNG, extension.substring(1)));
            }
            // unique filename
            String filename = UUID.randomUUID() + extension;

            // uploads/avatars klasörüne kaydet
            Path uploadPath = Path.of("uploads/avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // DB update
            user.setImageUrl("/uploads/avatars/" + filename);
            userRepository.save(user);

            return new ImageUploadResponseDto(user.getImageUrl());

        } catch (IOException e) {
            throw new BaseException(new ErrorMessage(MessageType.FILE_UPLOAD_ERROR, e.getMessage()));
        }


    }

    @Override
    public void changePassword(String authUsername, ChangePasswordRequestDto input) {
        User user = userRepository.findByUsername(authUsername)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, authUsername)));

        if(!passwordEncoder.matches(input.getOldPassword(), user.getPassword())){
            throw new BaseException(new ErrorMessage(MessageType.OLD_PASSWORD_IS_WRONG, ""));
        }
        if (!input.getNewPassword().equals(input.getConfirmPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.CONFIRM_PASSWORD_ERROR, input.getConfirmPassword()));
        }
        if (passwordEncoder.matches(input.getNewPassword(), user.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORDS_CANNOT_BE_SAME, input.getNewPassword()));
        }

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);
    }

    private UserProfileDto toUserProfileDto(User user, int page, int size){
        long postCount = postRepository.countByAuthor(user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));
        Page<Post> posts = postRepository.findAllByAuthor(user, pageable);

        List<PostCardDto> postDtos = posts.stream()
                .map(this::toPostCardDto)
                .toList();

        return UserProfileDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .joinedDate(user.getCreatedDate())
                .postsCount(postCount)
                .posts(postDtos)
                .build();
    }
    private MyProfileDto toMyProfileDto(User user, int page, int size){
        MyProfileDto profileDto = new MyProfileDto();
        UserProfileDto baseDto = toUserProfileDto(user, page, size);

        profileDto.setUsername(baseDto.getUsername());
        profileDto.setFirstName(baseDto.getFirstName());
        profileDto.setLastName(baseDto.getLastName());
        profileDto.setImageUrl(baseDto.getImageUrl());
        profileDto.setJoinedDate(baseDto.getJoinedDate());
        profileDto.setPostsCount(baseDto.getPostsCount());
        profileDto.setPosts(baseDto.getPosts());

        profileDto.setEmail(user.getEmail());
        profileDto.setBirthOfDate(user.getBirthOfDate());
        return profileDto;
    }

    private PostCardDto toPostCardDto(Post post){
        PostCardDto dto = new PostCardDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getUrlSlug());
        dto.setPublishedDate(post.getPublishedDate());
        dto.setImageUrl(post.getImageUrl());
        return dto;
    }
}
