package com.nurlan.service.interfaces.admin;

import com.nurlan.dto.admin.UserAdminDetailDto;
import com.nurlan.dto.admin.UserAdminListDto;
import com.nurlan.dto.admin.UserAdminUpdateDto;
import com.nurlan.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminUserService {

    Page<UserAdminListDto> listUsers(Boolean enabled, Role role, String q, Pageable pageable);

    UserAdminDetailDto getUser(Long id);

    UserAdminDetailDto updateUser(Long id, UserAdminUpdateDto dto);

    void setUserRole(Long id, Role role);

    void setUserEnabled(Long id, boolean enabled);

    void deleteUser(Long id);

}
