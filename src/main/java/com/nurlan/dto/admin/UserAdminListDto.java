package com.nurlan.dto.admin;

import com.nurlan.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminListDto {

    private Long id;
    private String username;
    private String email;
    private Role role;      // "ADMIN" | "USER"
    private boolean enabled;
    private Date createdDate;
}
