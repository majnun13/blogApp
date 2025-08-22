package com.nurlan.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {

    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Long postsCount;
    private Date joinedDate;
    private List<PostCardDto> posts;

}
