package com.nurlan.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponseDto {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Date createdDate;

}
