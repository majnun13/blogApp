package com.nurlan.service.impl.admin;

import com.nurlan.dto.AuthorDto;
import com.nurlan.dto.admin.CommentAdminListDto;
import com.nurlan.dto.admin.CommentAdminUpdateDto;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Comment;
import com.nurlan.repository.CommentRepository;
import com.nurlan.repository.PostRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.admin.IAdminCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminCommentServiceImpl implements IAdminCommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository  postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<CommentAdminListDto> list(Boolean enabled, Long postId, String q, Pageable pageable) {
        Page<Comment> page = commentRepository.adminSearch(enabled, postId, (q == null ? null : q.trim()), pageable);
        return page.map(this::toDto);
    }

    @Override
    public CommentAdminListDto getById(Long id) {
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, id.toString())));
        return toDto(c);
    }
    @Override
    public CommentAdminListDto update(Long id, CommentAdminUpdateDto dto) {
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, id.toString())));

        if (dto.getContent() != null) c.setContent(dto.getContent().trim());
        if (dto.getEnabled() != null) c.setEnabled(dto.getEnabled());

        return toDto(commentRepository.save(c));
    }

    @Override
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, id.toString()));
        }
        commentRepository.deleteById(id);
    }



    private CommentAdminListDto toDto(Comment c) {
        var u = c.getUser();
        var p = c.getPost();
        AuthorDto author = new AuthorDto(
                u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getImageUrl(), u.getRole()
        );
        return CommentAdminListDto.builder()
                .id(c.getId())
                .postId(p != null ? p.getId() : null)
                .postTitle(p != null ? p.getTitle() : null)
                .author(author)
                .content(c.getContent())
                .enabled(c.isEnabled())
                .createdDate(c.getCreatedDate())
                .build();
    }
}
