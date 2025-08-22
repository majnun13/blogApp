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
public class UserAdminDetailDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Role role;      // "ADMIN" | "USER"
    private boolean enabled;
    private boolean banned;
    private Date createdDate;
    private Date birthOfDate;

}
