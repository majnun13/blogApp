package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.IAdminUserController;
import com.nurlan.dto.admin.UserAdminDetailDto;
import com.nurlan.dto.admin.UserAdminListDto;
import com.nurlan.dto.admin.UserAdminRoleUpdate;
import com.nurlan.dto.admin.UserAdminUpdateDto;
import com.nurlan.enums.Role;
import com.nurlan.service.interfaces.admin.IAdminUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserControllerImpl implements IAdminUserController {

    @Autowired
    private IAdminUserService adminService;


    @Override
    @GetMapping(value = "/list")
    public Page<UserAdminListDto> list(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false)Role role,
            @RequestParam(required = false)String q,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC)Pageable pageable) {
        return adminService.listUsers(enabled, role, q, pageable);
    }

    @Override
    @GetMapping(value = "/{id}")
    public UserAdminDetailDto get(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @Override
    @PutMapping(value = "/{id}")
    public UserAdminDetailDto update(@PathVariable Long id,
                                     @RequestBody @Valid UserAdminUpdateDto dto) {
        return adminService.updateUser(id, dto);
    }

    @Override
    @PutMapping(value = "/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setRole(@PathVariable Long id,
                        @RequestBody @Valid UserAdminRoleUpdate body) {
        adminService.setUserRole(id, body.getRole());
    }

    @Override
    @PutMapping(value = "/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEnabled(@PathVariable Long id,
                           @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", true);
        adminService.setUserEnabled(id, enabled);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        adminService.deleteUser(id);

    }
}
