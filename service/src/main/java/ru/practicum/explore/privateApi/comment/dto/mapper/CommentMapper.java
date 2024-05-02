package ru.practicum.explore.privateApi.comment.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.comment.dto.InCommentDto;
import ru.practicum.explore.privateApi.comment.model.Comment;
import ru.practicum.explore.privateApi.event.model.Event;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", source = "created", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "author", source = "user")
    Comment toComment(InCommentDto dto, LocalDateTime created, Event event, User user);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentDto toCommentDto(Comment comment);
}
