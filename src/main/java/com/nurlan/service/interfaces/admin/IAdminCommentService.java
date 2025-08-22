package com.nurlan.service.interfaces.admin;

import com.nurlan.dto.admin.CommentAdminListDto;
import com.nurlan.dto.admin.CommentAdminUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminCommentService {

    Page<CommentAdminListDto> list(Boolean enabled, Long postId, String q, Pageable pageable);

    CommentAdminListDto getById(Long id);

    CommentAdminListDto update(Long id, CommentAdminUpdateDto dto);

    void delete(Long id);
}
