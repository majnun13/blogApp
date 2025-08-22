package com.nurlan.dto;

import com.nurlan.enums.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Role role;
}
