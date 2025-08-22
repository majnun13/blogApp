package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.IPostController;
import com.nurlan.dto.PostCreateDto;
import com.nurlan.dto.PostResponseDto;
import com.nurlan.dto.PostUpdateDto;
import com.nurlan.dto.TagInfoDto;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.User;
import com.nurlan.repository.UserRepository;
import com.nurlan.service.interfaces.IPostService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostControllerImpl implements IPostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private UserRepository userRepository;

    private static final int MAX_PAGE_SIZE = 50;


    @Override
    @GetMapping
    public Page<PostResponseDto> list(
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tags) {
        // 1) tags varsa: tag filtrelemesi (OR)
        if (tags != null && !tags.isBlank()) {
            List<String> names = Arrays.stream(tags.split(","))
                    .map(s -> s.trim())
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            return postService.findPublishedByTagsOr(names, pageable);
        }

        // 2) q varsa: mevcut text search
        if (q != null && !q.isBlank()) {
            return postService.searchPublished(q.trim(), pageable);
        }

        // 3) default yayınlanmışlar
        return postService.getPublished(pageable);

    }

    @Override
    @GetMapping("/{slug}")
    public PostResponseDto detail(@PathVariable String slug,
                                  @RequestParam(defaultValue = "0") int cpage,
                                  @RequestParam(defaultValue = "10") int csize) {
        int page = Math.max(0, cpage);
        int size = Math.min(Math.max(1, csize), MAX_PAGE_SIZE);
        return postService.getBySlugWithComments(slug, page, size);
    }

    @Override
    @PutMapping("/{slug}/tags")
    public PostResponseDto setTags(@PathVariable String slug,
                                   @Valid @RequestBody List<TagInfoDto> items,
                                   Principal principal) {
        return postService.setTags(slug, items, principal.getName());
    }

    @Override
    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponseDto create(
            @Valid @RequestPart("data") PostCreateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication auth) {
        String username = auth.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));

        return postService.create(dto, author, image);
    }

    @Override
    @GetMapping("/mine")
    public ResponseEntity<Page<PostResponseDto>> getMyPosts(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal
    ) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        Page<PostResponseDto> page = postService.getMine(principal.getName(), safePageable);
        return ResponseEntity.ok(page);
    }


    @PutMapping(value = "/update/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<PostResponseDto> update(
            @PathVariable String slug,
            @Valid @RequestPart("data") PostUpdateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "removeImage", required = false) Boolean removeImage,
            Principal principal
    ) {
        PostResponseDto resp = postService.update(slug, dto, principal.getName(), image, removeImage);
        return ResponseEntity.ok(resp);
    }

    @Override
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug, Principal principal) {
        postService.delete(slug, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
