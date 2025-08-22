package com.nurlan.service.interfaces.admin;

import com.nurlan.dto.admin.PostAdminDetailDto;
import com.nurlan.dto.admin.PostAdminListDto;
import com.nurlan.dto.admin.PostAdminUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface IAdminPostService {
    Page<PostAdminListDto> list(
            Optional<Boolean> published,
            Optional<Boolean> enabled,
            Optional<Long> authorId,
            Optional<String> q,
            Pageable pageable
    );

    PostAdminDetailDto getById(Long id);

    PostAdminDetailDto update(Long id, PostAdminUpdateDto dto);

    // Hard delete
    void delete(Long id);
}
