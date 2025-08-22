package com.nurlan.controller.impl;

import com.nurlan.controller.interfaces.IAdminCommentController;
import com.nurlan.dto.admin.CommentAdminListDto;
import com.nurlan.dto.admin.CommentAdminUpdateDto;
import com.nurlan.service.interfaces.admin.IAdminCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentControllerImpl implements IAdminCommentController {

    @Autowired
    private IAdminCommentService adminService;

    @Override
    @GetMapping("/list")
    public Page<CommentAdminListDto> list(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return adminService.list(enabled, postId, q, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public CommentAdminListDto getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    public CommentAdminListDto update(@PathVariable Long id,
                                      @RequestBody CommentAdminUpdateDto input) {
        return adminService.update(id, input);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        adminService.delete(id);
    }
}
