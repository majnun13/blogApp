package com.nurlan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostCardDto {

    private Long id;
    private String title;
    private String slug;
    private String imageUrl;
    private Date publishedDate;

}
