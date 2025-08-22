package com.nurlan.service.impl;

import com.nurlan.dto.*;
import com.nurlan.enums.TagColor;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Comment;
import com.nurlan.models.Post;
import com.nurlan.models.Tag;
import com.nurlan.models.User;
import com.nurlan.repository.CommentRepository;
import com.nurlan.repository.PostRepository;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.IPostService;
import com.nurlan.service.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ITagService tagService;


    private static final Path UPLOAD_ROOT = Path.of("uploads");
    private static final String POST_DIR   = "posts";
    private static final long MIN_SIZE = 100L * 1024;   // 100 KB
    private static final long MAX_SIZE = 10L * 1024 * 1024;
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPublished(Pageable pageable) {
        return postRepository
                .findByPublishedTrueAndEnabledTrue(pageable)
                .map(this::toPostDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPublished(String q, Pageable pageable) {
        if(q == null || q.trim().isEmpty())
            return getPublished(pageable);

        return postRepository.findByTitleContainingIgnoreCaseAndPublishedTrueAndEnabledTrue(q.trim(), pageable)
                .map(this::toPostDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getBySlug(String slug) {
        Post post = postRepository.findByUrlSlug(slug)
                .filter(p-> p.isPublished())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug)));

        return toPostDto(post);
    }

    @Override
    public PostResponseDto create(PostCreateDto dto, User author, MultipartFile image) {

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setContent(dto.getContent());

        String base = slugify(dto.getTitle());
        String unique = ensureUniqueSlug(base);
        post.setUrlSlug(unique);
        post.setAuthor(author);
        post.setPublished(true);
        post.setPublishedDate(new Date());

        String imageUrl = savePostImage(image);
        if(imageUrl != null)
            post.setImageUrl(imageUrl);


        Post saved = postRepository.save(post);
        return toPostDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getBySlugWithComments(String slug, int commentPage, int commentSize) {
        Post post = postRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug)));
        if(!post.isPublished())
            throw new BaseException(new ErrorMessage(MessageType.POST_IS_NOT_PUBLISHED, slug));

        Pageable pageable = PageRequest.of(
                commentPage,
                commentSize,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        Page<Comment> page = commentRepository.findByPostIdAndEnabledTrue(post.getId(), pageable);
        PostResponseDto postDto = toPostDto(post);

        long enabledCount = commentRepository.countByPostIdAndEnabledTrue(post.getId());
        postDto.setCommentCount(Math.toIntExact(enabledCount));

        List<CommentResponseDto> comments = page.getContent()
                .stream()
                .map(this::toCommentDto)
                .toList();
        postDto.setComments(comments);

        return postDto;
    }

    @Override
    public Page<PostResponseDto> getMine(String username, Pageable pageable) {
        return postRepository
                .findByAuthorUsernameOrderByIdDesc(username, pageable)
                .map(this::toPostDto);
    }


    private void assertOwner(Post post, User me) {
        boolean isOwner = post.getAuthor() != null &&
                Objects.equals(post.getAuthor().getId(), me.getId());
        if (!isOwner) {
            throw new BaseException(new ErrorMessage(
                    MessageType.ACCESS_DENIED,
                    "Bu post üzerinde işlem yapma yetkin yok"
            ));
        }
    }


    @Override
    @Transactional
    public PostResponseDto update(String slug,
                                  PostUpdateDto dto,
                                  String username,
                                  MultipartFile image,
                                  Boolean removeImage) {
        Post post = postRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug)));

        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));

        assertOwner(post, me);

        if (dto.getTitle() != null)       post.setTitle(dto.getTitle());
        if (dto.getDescription() != null) post.setDescription(dto.getDescription());
        if (dto.getContent() != null)     post.setContent(dto.getContent());

        if (dto.getPublish() != null) {
            boolean wantPublish = dto.getPublish();
            if (wantPublish && !post.isPublished()) {
                post.setPublished(true);
                post.setPublishedDate(new Date());
            } else if (!wantPublish) {
                post.setPublished(false);
            }
        }

        String oldUrl = post.getImageUrl();

        boolean askedRemove =
                Boolean.TRUE.equals(removeImage) || Boolean.TRUE.equals(dto.getRemoveImage());

        if (askedRemove && oldUrl != null) {
            deleteUrlIfExists(oldUrl);
            post.setImageUrl(null);
            oldUrl = null;
        }

        if (image != null && !image.isEmpty()) {
            if (oldUrl != null) deleteUrlIfExists(oldUrl);
            String newUrl = savePostImage(image);  // "/uploads/posts/2025-08-18/uuid.png"
            post.setImageUrl(newUrl);
        }

        Post saved = postRepository.save(post);
        return toPostDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> findPublishedByTagsOr(List<String> tagNames, Pageable pageable) {

        List<String> lowers = tagNames.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if(lowers.isEmpty())
            return Page.empty(pageable);

        return postRepository.findPublishedByAnyTag(lowers, pageable).map(this::toPostDto);
    }

    @Override
    @Transactional
    public PostResponseDto setTags(String slug, List<TagInfoDto> items, String username) {

        Post post = postRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug)));

        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));

        assertOwner(post, me);
        List<Tag> newTags = new ArrayList<>();
        if(items != null){
            Set<String> seen = new HashSet<>();
            for(TagInfoDto it : items){
                if (it == null || it.getName() == null) continue;
                String name = it.getName().trim().replaceAll("\\s{2,}", " ");
                if (name.isEmpty() || !seen.add(name.toLowerCase())) continue;

                TagColor colorEnum = null;
                if(it.getColor() != null && !it.getColor().isBlank()){
                    try{
                        colorEnum = TagColor.valueOf(it.getColor().toUpperCase());
                    }catch(IllegalArgumentException e){
                        throw new BaseException(new ErrorMessage(MessageType.INVALID_TAG_COLOR, it.getColor()));
                    }
                }
                newTags.add(tagService.getOrCreateByName(name, colorEnum));
            }
        }
        post.setTags(newTags);
        Post saved = postRepository.save(post);
        return toPostDto(saved);
    }


    @Override
    public void delete(String slug, String username) {
        Post post = postRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.POST_NOT_FOUND, slug)));

        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));

        assertOwner(post, me);

        postRepository.delete(post);
    }

    // --- Helpers ---

    // /uploads/posts/{YYYY-MM-DD}/{UUID}.{ext} kaydeder, dönen değer: "/uploads/posts/..."
    public String savePostImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        long size = file.getSize();
        if (size < MIN_SIZE) {
            throw new BaseException(new ErrorMessage(
                    MessageType.FILE_MUST_BE_BIGGER_THAN_100_KBPS, file.getOriginalFilename()));
        }
        if (size > MAX_SIZE) {
            throw new BaseException(new ErrorMessage(
                    MessageType.FILE_MUST_BE_SMALLER_THAN_10_MB, file.getOriginalFilename()));
        }

        String original = file.getOriginalFilename();
        if (original == null) {
            throw new BaseException(new ErrorMessage(MessageType.IMAGE_FILE_NAME_NOT_FOUND, ""));
        }

        String ext = original.substring(original.lastIndexOf(".")).toLowerCase();
        if (!ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png") && !ext.equals(".webp")) {
            throw new BaseException(new ErrorMessage(
                    MessageType.FILE_MUST_BE_JPG_OR_PNG, ext.replace(".", "")));
        }

        try {
            String folder = java.time.LocalDate.now().toString(); // 2025-08-18
            java.nio.file.Path dir = UPLOAD_ROOT.resolve(POST_DIR).resolve(folder);
            if (!java.nio.file.Files.exists(dir)) java.nio.file.Files.createDirectories(dir);

            String filename = java.util.UUID.randomUUID() + ext;
            java.nio.file.Path target = dir.resolve(filename);

            java.nio.file.Files.copy(file.getInputStream(), target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + POST_DIR + "/" + folder + "/" + filename;
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessageType.FILE_UPLOAD_ERROR, e.getMessage()));
        }
    }

    private void deleteUrlIfExists(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return;
        // "/uploads/posts/..../file.png"  -> disk: "uploads/posts/..../file.png"
        try {
            String noSlash = urlPath.startsWith("/") ? urlPath.substring(1) : urlPath;
            Files.deleteIfExists(Path.of(noSlash));
        } catch (Exception ignored) {}
    }

    private String slugify(String input) {
        if (input == null) return "";
        // Türkçe karakterleri sadeleştir + non-latinleri at
        String nowhitespace = input.trim().toLowerCase();
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // aksanları temizle
        String slug = normalized
                .replaceAll("[^a-z0-9\\s-]", "")   // geçersizleri at
                .replaceAll("\\s+", "-")           // boşluk → -
                .replaceAll("-{2,}", "-")          // fazla - düzelt
                .replaceAll("^-|-$", "");          // baş/son -
        if (slug.isBlank()) slug = "post";
        return slug;
    }

    private String ensureUniqueSlug(String base) {
        String candidate = base;
        int i = 2;
        while (postRepository.existsByUrlSlug(candidate)) {
            candidate = base + "-" + i;
            i++;
        }
        return candidate;
    }
    private PostResponseDto toPostDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setUrlSlug(post.getUrlSlug());
        dto.setPublished(post.isPublished());
        dto.setPublishedDate(post.getPublishedDate());

        if (post.getAuthor() != null) {
            AuthorDto author = new AuthorDto();
            author.setUsername(post.getAuthor().getUsername());
            author.setAvatarUrl(post.getAuthor().getImageUrl());
            author.setId(post.getAuthor().getId());
            author.setFirstName(post.getAuthor().getFirstName());
            author.setLastName(post.getAuthor().getLastName());
            dto.setAuthor(author);
        }
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            List<TagInfoDto> tagDtos = post.getTags().stream().map(tag -> {
                TagInfoDto t = new TagInfoDto();
                t.setName(tag.getName());
                t.setColor(tag.getColor() != null ? tag.getColor().name() : null); // enum -> String
                return t;
            }).toList();
            dto.setTags(tagDtos);
        }

        return dto;
    }

    private CommentResponseDto toCommentDto(Comment comment){
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());

        AuthorDto author = new AuthorDto();
        author.setUsername(comment.getUser().getUsername());
        author.setFirstName(comment.getUser().getFirstName());
        author.setLastName(comment.getUser().getLastName());
        author.setAvatarUrl(comment.getUser().getImageUrl());
        author.setId(comment.getUser().getId());
        dto.setAuthor(author);

        return dto;
    }
}
