package com.nurlan.controller.interfaces;

import com.nurlan.dto.admin.UserAdminDetailDto;
import com.nurlan.dto.admin.UserAdminListDto;
import com.nurlan.dto.admin.UserAdminRoleUpdate;
import com.nurlan.dto.admin.UserAdminUpdateDto;
import com.nurlan.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface IAdminUserController {

    Page<UserAdminListDto> list(Boolean enabled, Role role, String q, Pageable pageable);

    UserAdminDetailDto get(Long id);

    UserAdminDetailDto update(Long id, UserAdminUpdateDto dto);

    void setRole(Long id, UserAdminRoleUpdate body);

    void setEnabled(Long id, Map<String, Boolean> body);

    void delete(Long id);
}
