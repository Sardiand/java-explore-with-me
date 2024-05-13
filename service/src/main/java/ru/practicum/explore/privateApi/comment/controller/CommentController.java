package ru.practicum.explore.privateApi.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.comment.dto.InCommentDto;
import ru.practicum.explore.privateApi.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Validated
public class CommentController {
    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @Positive Long userId,
                                 @RequestParam(name = "event") @Positive Long eventId,
                                 @RequestBody @Valid InCommentDto dto) {
        return service.create(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId,
                                    @RequestBody @Valid InCommentDto dto) {
        return service.update(userId, commentId, dto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        return service.getById(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getUsersComments(@PathVariable @Positive Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return service.getAllByUserId(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.deleteById(userId, commentId);
    }
}
