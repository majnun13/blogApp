package com.nurlan.service.impl;

import com.nurlan.dto.AuthorDto;
import com.nurlan.dto.CommentCreateDto;
import com.nurlan.dto.CommentResponseDto;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Comment;
import com.nurlan.models.Post;
import com.nurlan.models.User;
import com.nurlan.repository.CommentRepository;
import com.nurlan.repository.PostRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.ICommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Override
    @Transactional
    public CommentResponseDto createComment(String slug, CommentCreateDto input, Long currentUserId) {

        Optional<Post> post = postRepository.findByUrlSlug(slug);
        Optional<User> user = userRepository.findById(currentUserId);
        Comment savedComment;
        if(post.isPresent()){
            if(user.isPresent()){
                Comment comment = new Comment();
                comment.setPost(post.get());
                comment.setUser(user.get());
                comment.setCreatedDate(new Date());
                comment.setContent(input.getContent().trim());
                 savedComment = commentRepository.save(comment);
            }else {
                throw new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, currentUserId.toString()));
            }
        }else{
            throw new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug));
        }

        return toCommentDto(savedComment, user.get());
    }

    @Override
    @Transactional
    public void deleteComment(String postSlug, Long commentId, Long currentUserId) {
        Post post = postRepository.findByUrlSlug(postSlug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, postSlug)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, commentId.toString())));
        if(!comment.getPost().getId().equals(post.getId())){
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "Bu yorum belirtilen post'a ait değil"));
        }
        if(!comment.getUser().getId().equals(currentUserId)){
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, comment.getUser().getUsername()));
        }

        commentRepository.delete(comment);

    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(String postSlug, Long commentId, Long currentUserId, CommentCreateDto input) {

        Post post = postRepository.findByUrlSlug(postSlug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, postSlug)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, commentId.toString())));
        if(!comment.getPost().getId().equals(post.getId())){
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "Bu yorum belirtilen post'a ait değil"));
        }
        if(!comment.getUser().getId().equals(currentUserId)){
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, comment.getUser().getUsername()));
        }

        comment.setContent(input.getContent().trim());
        Comment savedComment = commentRepository.save(comment);

        return toCommentDto(comment, savedComment.getUser());
    }

    private CommentResponseDto toCommentDto(Comment comment){
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        return dto;
    }

    private CommentResponseDto toCommentDto(Comment comment, User user){
        CommentResponseDto dto = toCommentDto(comment);
        dto.setAuthor(toAuthorDto(user));
        return dto;
    }
    private AuthorDto toAuthorDto(User user){
        AuthorDto dto = new AuthorDto();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAvatarUrl(user.getImageUrl());
        dto.setId(user.getId());
        return dto;
    }
}
