package com.nurlan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto
{
    private String username;
    private String firstName;
    private String lastName;
    private Date birthOfDate;
    private String currentPassword;
}
