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
public class UserAdminUpdateDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Boolean enabled;   // opsiyonel alanlar -> Boolean
    private Boolean banned;
    private Date birthOfDate;
}
