package com.nurlan.controller.interfaces;

import com.nurlan.dto.CommentCreateDto;
import com.nurlan.dto.CommentResponseDto;
import org.springframework.security.core.Authentication;

public interface ICommentController {

    public CommentResponseDto createComment(String slug, CommentCreateDto input, Authentication auth);
    void delete(String slug, Long commentId, org.springframework.security.core.Authentication auth);
    CommentResponseDto update(String slug, Long commentId, CommentCreateDto dto, Authentication auth);

}
