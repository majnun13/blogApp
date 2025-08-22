package com.nurlan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentResponseDto {

    private Long id;
    private String content;
    private Date createdDate;
    private AuthorDto author;

}
