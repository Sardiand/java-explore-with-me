package ru.practicum.explore.privateApi.comment.service;

import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.comment.dto.InCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(Long userId, Long eventId, InCommentDto dto);

    CommentDto update(Long userId, Long commentId, InCommentDto dto);

    CommentDto getById(Long userId, Long commentId);

    List<CommentDto> getAllByUserId(Long userId, int from, int size);

    void deleteById(Long userId, Long commentId);
}
