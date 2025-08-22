    package com.nurlan.controller.impl;

    import com.nurlan.controller.interfaces.IAdminPostController;
    import com.nurlan.dto.admin.PostAdminDetailDto;
    import com.nurlan.dto.admin.PostAdminListDto;
    import com.nurlan.dto.admin.PostAdminUpdateDto;
    import com.nurlan.exception.BaseException;
    import com.nurlan.exception.ErrorMessage;
    import com.nurlan.exception.MessageType;
    import com.nurlan.service.interfaces.IPostService;
    import com.nurlan.service.interfaces.admin.IAdminPostService;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.MediaType;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.Optional;

    @Slf4j
    @RestController
    @RequestMapping("/api/admin/posts")
    @PreAuthorize("hasRole('ADMIN')")
    @Validated
    public class AdminPostControllerImpl implements IAdminPostController {

        @Autowired
        private IAdminPostService adminService;

        @Autowired
        private IPostService postService;
        @Override
        @GetMapping(value = "/list")
        public Page<PostAdminListDto> list(
                @RequestParam(required = false) Boolean published,
                @RequestParam(required = false) Boolean enabled,
                @RequestParam(required = false) Long authorId,
                @RequestParam(required = false) String q,
                Pageable pageable) {
            return adminService.list(
                    Optional.ofNullable(published),
                    Optional.ofNullable(enabled),
                    Optional.ofNullable(authorId),
                    Optional.ofNullable(q),
                    pageable
            );
        }

        @Override
        @GetMapping(value = "/{id}")
        public PostAdminDetailDto getById(@PathVariable Long id) {
            return adminService.getById(id);
        }

        @Override
        @PutMapping(value = "/{id}")
        public PostAdminDetailDto update(@PathVariable Long id,
                                         @RequestBody PostAdminUpdateDto dto) {
            return adminService.update(id, dto);
        }

        @Override
        @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public PostAdminDetailDto uploadImageAndUpdate(
                @PathVariable Long id,
                @RequestPart("file") MultipartFile file) {
            if (file == null || file.isEmpty()) {
                throw new BaseException(new ErrorMessage(MessageType.IMAGE_FILE_IS_EMPTY, "file"));
            }
            String imageUrl = postService.savePostImage(file);

            var dto = PostAdminUpdateDto.builder()
                    .imageUrl(imageUrl)
                    .build();

            return adminService.update(id, dto);
        }

        @Override
        @DeleteMapping(value = "/{id}")
        public void delete(Long id) {
            adminService.delete(id);
        }
    }
