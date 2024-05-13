package ru.practicum.explore.admin.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository commentRepository;

    @Override
    public void deleteComment(Long commentId) {
        if (commentRepository.findById(commentId).isEmpty()) {
            throw new NotFoundException(String.format("Comment not found %d", commentId));
        }
        commentRepository.deleteById(commentId);
    }
}
