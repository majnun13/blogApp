package com.nurlan.dto.admin;

import com.nurlan.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminRoleUpdate {

    private Role role;

}
