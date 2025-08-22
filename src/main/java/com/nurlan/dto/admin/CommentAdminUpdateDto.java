package com.nurlan.dto.admin;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentAdminUpdateDto {
    private String content;
    private Boolean enabled; // opsiyonel
}