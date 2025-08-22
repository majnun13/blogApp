package com.nurlan.controller.interfaces;

import com.nurlan.dto.PostCreateDto;
import com.nurlan.dto.PostResponseDto;
import com.nurlan.dto.PostUpdateDto;
import com.nurlan.dto.TagInfoDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface IPostController {

    Page<PostResponseDto> list(Pageable pageable,
                               @RequestParam(required = false) String q,
                               @RequestParam(required = false) String tags);
    PostResponseDto detail(String slug, int commentPage, int commentSize);
    PostResponseDto setTags(@PathVariable String slug,
                            @RequestBody @Valid List<TagInfoDto> items,
                            Principal principal);
    PostResponseDto create(@Valid PostCreateDto dto, MultipartFile image,
                           org.springframework.security.core.Authentication auth);

    ResponseEntity<Page<PostResponseDto>> getMyPosts(Pageable pageable, Principal principal);
    ResponseEntity<PostResponseDto> update(String slug,@Valid PostUpdateDto dto,  MultipartFile image,
                                           Boolean removeImage, Principal principal);

    ResponseEntity<Void> delete(String slug, Principal principal);
}
