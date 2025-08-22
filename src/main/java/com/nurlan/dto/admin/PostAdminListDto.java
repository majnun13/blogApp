package com.nurlan.dto.admin;

import com.nurlan.dto.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostAdminListDto {

    private Long id;
    private String title;
    private AuthorDto author;
    private boolean published;
    private boolean enabled;
}
