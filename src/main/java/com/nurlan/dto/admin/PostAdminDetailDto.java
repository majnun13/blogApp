// com.nurlan.dto.admin.post.PostAdminDetailDto
package com.nurlan.dto.admin;

import com.nurlan.dto.AuthorDto;
import com.nurlan.dto.TagInfoDto;
import lombok.*;
import java.util.Date;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostAdminDetailDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String urlSlug;
    private boolean published;
    private boolean enabled;
    private AuthorDto author;       // <— burada da AuthorDto
    private Date publishedDate;
    private List<TagInfoDto> tags;
}
