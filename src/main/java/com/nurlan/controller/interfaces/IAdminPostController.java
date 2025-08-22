package com.nurlan.controller.interfaces;

import com.nurlan.dto.admin.PostAdminDetailDto;
import com.nurlan.dto.admin.PostAdminListDto;
import com.nurlan.dto.admin.PostAdminUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface IAdminPostController {

    Page<PostAdminListDto> list(
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String q,
            Pageable pageable);

    PostAdminDetailDto getById(Long id);
    PostAdminDetailDto update(Long id, PostAdminUpdateDto dto);
    PostAdminDetailDto uploadImageAndUpdate(Long id,
                                            MultipartFile file);
    void delete(Long id);


}
