package com.nurlan.service.interfaces;

import com.nurlan.dto.PostCreateDto;
import com.nurlan.dto.PostResponseDto;
import com.nurlan.dto.PostUpdateDto;
import com.nurlan.dto.TagInfoDto;
import com.nurlan.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IPostService {

    Page<PostResponseDto> getPublished(Pageable pageable);
    Page<PostResponseDto> searchPublished(String q, Pageable pageable);
    PostResponseDto getBySlug(String slug);

    PostResponseDto create(PostCreateDto dto, User author, MultipartFile image);
    // IPostService.java

    PostResponseDto getBySlugWithComments(String slug, int commentPage, int commentSize);

    Page<PostResponseDto> getMine(String username, Pageable pageable);

    PostResponseDto update(String slug, PostUpdateDto dto, String username, MultipartFile image, Boolean removeImage);
    Page<PostResponseDto> findPublishedByTagsOr(java.util.List<String> tagNames, Pageable pageable);

    String savePostImage(MultipartFile file);

        PostResponseDto setTags(String slug, java.util.List<TagInfoDto> items, String username);
    void delete(String slug, String username);
}
