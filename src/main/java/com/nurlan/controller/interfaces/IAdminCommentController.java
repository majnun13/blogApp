package com.nurlan.controller.interfaces;

import com.nurlan.dto.admin.CommentAdminListDto;
import com.nurlan.dto.admin.CommentAdminUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminCommentController {
    Page<CommentAdminListDto> list(Boolean enabled, Long postId, String q, Pageable pageable);

    CommentAdminListDto getById(Long id);

    CommentAdminListDto update(Long id, CommentAdminUpdateDto input);

    void delete(Long id);
}
