package com.nurlan.service.interfaces;

import com.nurlan.dto.CommentCreateDto;
import com.nurlan.dto.CommentResponseDto;

public interface ICommentService {

    public CommentResponseDto createComment(String slug, CommentCreateDto input, Long currentUserId);

    void deleteComment(String postSlug, Long commentId, Long currentUserId);

    CommentResponseDto updateComment(String postSlug, Long commentId, Long currentUserId, CommentCreateDto input);
}
