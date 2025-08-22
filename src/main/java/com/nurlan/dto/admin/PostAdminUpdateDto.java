package com.nurlan.dto.admin;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostAdminUpdateDto {
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String urlSlug;
    private Boolean published;  // opsiyonel
    private Boolean enabled;    // opsiyonel
    private List<Long> tagIds;  // M2M sync
}