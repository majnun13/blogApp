package com.nurlan.dto.admin;

import com.nurlan.dto.AuthorDto;
import lombok.*;
import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentAdminListDto {
    private Long id;
    private Long postId;
    private String postTitle;
    private AuthorDto author;
    private String content;
    private boolean enabled;
    private Date createdDate; // entity ile aynı
}