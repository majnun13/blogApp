package com.nurlan.service.impl.admin;

import com.nurlan.dto.admin.UserAdminDetailDto;
import com.nurlan.dto.admin.UserAdminListDto;
import com.nurlan.dto.admin.UserAdminUpdateDto;
import com.nurlan.enums.Role;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.User;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.admin.IAdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class AdminUserServiceImpl implements IAdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserAdminListDto> listUsers(Boolean enabled, Role role, String q, Pageable pageable) {

        boolean noFilter = (enabled == null) && (role == null) && (q == null || q.isBlank());

        if (noFilter) {
            // Tamamen DB pageable
            Page<User> page = userRepository.findAll(pageable);
            List<UserAdminListDto> dtos = page.getContent().stream()
                    .map(this::toListDto)
                    .toList();
            return new PageImpl<>(dtos, pageable, page.getTotalElements());
        }

        // Filtre VARSA: hepsini çek, filtrele, sonra manuel slice
        List<UserAdminListDto> all = userRepository.findAll().stream()
                .filter(u -> enabled == null || u.isEnabled() == enabled)
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> {
                    if (q == null || q.isBlank()) return true;
                    String needle = q.toLowerCase(Locale.ROOT);
                    return (u.getUsername() != null && u.getUsername().toLowerCase(Locale.ROOT).contains(needle))
                            || (u.getEmail() != null && u.getEmail().toLowerCase(Locale.ROOT).contains(needle));
                })
                .map(this::toListDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), all.size());
        List<UserAdminListDto> slice = (start >= end) ? List.of() : all.subList(start, end);

        return new PageImpl<>(slice, pageable, all.size());
    }

    @Override
    public UserAdminDetailDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, id.toString())));
        return toDetailDto(user);
    }

    @Override
    @Transactional
    public UserAdminDetailDto updateUser(Long id, UserAdminUpdateDto dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, id.toString())));

        // username unique (kendi haricinde)
        if (dto.getUsername() != null && !Objects.equals(dto.getUsername(), u.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
                throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, dto.getUsername()));
            }
            u.setUsername(dto.getUsername());
        }

        // email unique (kendi haricinde)
        if (dto.getEmail() != null && !Objects.equals(dto.getEmail(), u.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, dto.getEmail()));
            }
            u.setEmail(dto.getEmail());
        }

        if (dto.getFirstName() != null) u.setFirstName(dto.getFirstName());
        if (dto.getLastName()  != null) u.setLastName(dto.getLastName());
        if (dto.getImageUrl()  != null) u.setImageUrl(dto.getImageUrl());
        if (dto.getBirthOfDate() != null) u.setBirthOfDate(dto.getBirthOfDate());
        if (dto.getEnabled()   != null) u.setEnabled(dto.getEnabled());
        if (dto.getBanned()    != null) u.setBanned(dto.getBanned());

        User saved = userRepository.save(u);
        return toDetailDto(saved);
    }

    @Override
    @Transactional
    public void setUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, id.toString())));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setUserEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, id.toString())));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id))
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, id.toString()));
        userRepository.deleteById(id);
    }

    // ========== MAPPERS ==========
    private UserAdminListDto toListDto(User u) {
        return UserAdminListDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdDate(u.getCreatedDate())
                .build();
    }

    private UserAdminDetailDto toDetailDto(User u) {
        return UserAdminDetailDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .imageUrl(u.getImageUrl())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .banned(u.isBanned())
                .createdDate(u.getCreatedDate())
                .birthOfDate(u.getBirthOfDate())
                .build();
    }
}
