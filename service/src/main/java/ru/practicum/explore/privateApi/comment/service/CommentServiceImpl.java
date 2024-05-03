package ru.practicum.explore.privateApi.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.admin.user.repository.UserRepository;
import ru.practicum.explore.exception.ForbiddenException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.comment.dto.InCommentDto;
import ru.practicum.explore.privateApi.comment.dto.mapper.CommentMapper;
import ru.practicum.explore.privateApi.comment.model.Comment;
import ru.practicum.explore.privateApi.comment.repository.CommentRepository;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto create(Long userId, Long eventId, InCommentDto dto) {
        User user = setUser(userId);
        Event event = setEvent(eventId);
        return mapper.toCommentDto(commentRepository.save(mapper
                .toComment(dto, LocalDateTime.now(), event, user)));
    }

    @Override
    public CommentDto update(Long userId, Long commentId, InCommentDto dto) {
        checkUser(userId);
        Comment comment = setComment(commentId);
        checkPermission(userId, comment);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return mapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getById(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = setComment(commentId);
        checkPermission(userId, comment);
        return mapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllByUserId(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        } else {
            return commentRepository.findAllByAuthorId(userId, pageable).stream()
                    .map(mapper::toCommentDto).collect(Collectors.toList());
        }
    }

    @Override
    public void deleteById(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = setComment(commentId);
        checkPermission(userId, comment);
        commentRepository.deleteById(commentId);
    }

    private User setUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User not found %d", userId)));
    }

    private Event setEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", eventId)));
    }

    private Comment setComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment not found %d", commentId)));
    }

    private void checkPermission(Long userId, Comment comment) {
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ForbiddenException(String.format("User with id %d isn't author of comment %d",
                    userId, comment.getId()));
        }
    }

    private void checkUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User not found %d", userId));
        }
    }
}
