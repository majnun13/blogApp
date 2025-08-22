package com.nurlan.service.impl.admin;

import com.nurlan.dto.AuthorDto;
import com.nurlan.dto.TagInfoDto;
import com.nurlan.dto.admin.PostAdminDetailDto;
import com.nurlan.dto.admin.PostAdminListDto;
import com.nurlan.dto.admin.PostAdminUpdateDto;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Post;
import com.nurlan.models.Tag;
import com.nurlan.models.User;
import com.nurlan.repository.CommentRepository;
import com.nurlan.repository.PostRepository;
import com.nurlan.repository.TagRepository;
import com.nurlan.service.interfaces.IPostService;
import com.nurlan.service.interfaces.admin.IAdminPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class AdminPostServiceImpl implements IAdminPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IPostService postService;

    @Override
    @Transactional(readOnly = true)
    public Page<PostAdminListDto> list(
            Optional<Boolean> published,
            Optional<Boolean> enabled,
            Optional<Long> authorId,
            Optional<String> q,
            Pageable pageable) {

        Specification<Post> spec = (root, cq, cb) ->{
            List<Predicate> ps = new ArrayList<>();
            published.ifPresent(p -> ps.add(cb.equal(root.get("published"), p)));
            enabled .ifPresent(e -> ps.add(cb.equal(root.get("enabled") , e)));
            authorId.ifPresent(aid -> ps.add(cb.equal(root.get("author").get("id"), aid)));

            q.map(String::trim).filter(s -> !s.isEmpty()).ifPresent(query -> {
                String like = "%" + query.toLowerCase(Locale.ROOT) + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("urlSlug")), like)
                ));
            });
            if (ps.isEmpty()) {
                return cb.conjunction(); // TRUE predicate
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };

        return postRepository.findAll(spec, pageable).map(this::toListDto);

    }

    @Override
    @Transactional(readOnly = true)
    public PostAdminDetailDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, id.toString())));
        return toDetailDto(post);
    }

    @Override
    public PostAdminDetailDto update(Long id, PostAdminUpdateDto input) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, id.toString())));

        if(input.getTitle() != null){
            post.setTitle(input.getTitle());
        }
        if (input.getDescription() != null) {
            post.setDescription(input.getDescription().trim());
        }
        if (input.getContent() != null) {
            post.setContent(input.getContent());
        }
        if (input.getPublished() != null) {
            post.setPublished(input.getPublished());
            if (input.getPublished() && post.getPublishedDate() == null) {
                post.setPublishedDate(new Date());
            }
        }
        if (input.getEnabled() != null) {
            post.setEnabled(input.getEnabled());
        }
        if (input.getImageUrl() != null) {
            post.setImageUrl(input.getImageUrl().trim());
        }
        if (input.getTagIds() != null) {
            var ids = input.getTagIds();
            if (ids.isEmpty()) {
                post.getTags().clear();
            } else {
                var tags = tagRepository.findAllById(ids);
                if (tags.size() != ids.size()) {
                    throw new BaseException(new ErrorMessage(MessageType.INVALID_TAG, ids.toString()));
                }
                post.setTags(new ArrayList<>(tags));
            }
        }

        Post saved = postRepository.save(post);
        return toDetailDto(saved);
    }

    @Override
    public void delete(Long id) {
        if(!postRepository.existsById(id))
            throw new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, id.toString()));

        postRepository.deleteById(id);
    }


    /* ===================== MAPPERS ===================== */

    private PostAdminListDto toListDto(Post p) {
        return PostAdminListDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .author(toAuthorDto(p.getAuthor()))
                .published(p.isPublished())
                .enabled(p.isEnabled())
                .build();
    }

    private PostAdminDetailDto toDetailDto(Post p) {
        List<TagInfoDto> tagDtos = p.getTags() == null ? List.of()
                : p.getTags().stream()
                .sorted(Comparator.comparing(Tag::getName))
                .map(t -> new TagInfoDto(t.getId(), t.getName(), t.getColor().name()))
                .toList();

        return PostAdminDetailDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .urlSlug(p.getUrlSlug())          // urlSlug kullanıyoruz
                .published(p.isPublished())
                .enabled(p.isEnabled())
                .author(toAuthorDto(p.getAuthor()))
                .publishedDate(p.getPublishedDate())
                .tags(tagDtos)
                .build();
    }

    private AuthorDto toAuthorDto(User u) {
        if (u == null) return null;
        AuthorDto dto = new AuthorDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        return dto;
    }


}
