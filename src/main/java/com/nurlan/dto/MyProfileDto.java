package com.nurlan.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileDto extends UserProfileDto{

    private String email;
    private Date birthOfDate;

}
    