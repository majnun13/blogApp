package com.nurlan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PostResponseDto {

    private Long id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String urlSlug;

    private boolean published;
    private boolean approved;
    private Date publishedDate;

    private AuthorDto author;

    private List<TagInfoDto> tags;
    private List<CommentResponseDto> comments;

    private Integer commentCount;

}
