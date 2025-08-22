package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.ICommentController;
import com.nurlan.dto.CommentCreateDto;
import com.nurlan.dto.CommentResponseDto;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.ICommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class CommentControllerImpl implements ICommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Override
    @PostMapping("/{slug}/comments")
    public CommentResponseDto createComment(@PathVariable String slug, @Valid @RequestBody CommentCreateDto input, Authentication auth) {
        String username = auth.getName();
        Long currentUserId = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username))).getId();
        return commentService.createComment(slug, input, currentUserId);
    }

    @Override
    @DeleteMapping("/{slug}/comments/{id}")
    public void delete(@PathVariable String slug, @PathVariable("id") Long commentId, Authentication auth) {
        String username = auth.getName();
        Long currentUserId = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username))).getId();
        commentService.deleteComment(slug, commentId, currentUserId);
    }

    @Override
    @PutMapping("/{slug}/comments/{id}")
    public CommentResponseDto update(@PathVariable String slug, @PathVariable("id") Long commentId, @RequestBody CommentCreateDto dto, Authentication auth) {
        String username = auth.getName();
        Long currentUserId = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)))
                .getId();
        return commentService.updateComment(slug, commentId, currentUserId, dto);
    }
}
