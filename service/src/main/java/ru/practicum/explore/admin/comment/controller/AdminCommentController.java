package ru.practicum.explore.admin.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.admin.comment.service.AdminCommentService;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentController {
    private final AdminCommentService service;

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable @Positive Long commentId) {
        service.deleteComment(commentId);
    }
}
