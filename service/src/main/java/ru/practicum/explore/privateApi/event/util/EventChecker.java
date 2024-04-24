package ru.practicum.explore.privateApi.event.util;

import ru.practicum.explore.admin.event.dto.AdminUpdateEventDto;
import ru.practicum.explore.privateApi.event.dto.UserUpdateEventDto;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.State;

import java.util.function.Consumer;

public class EventChecker {

    public static <T> void setIfNonNull(final Consumer<T> tConsumer, final T value) {
        if (value != null) {
            tConsumer.accept(value);
        }
    }

    public static Event checkForAdmin(Event event, AdminUpdateEventDto dto, State state) {
        setIfNonNull(event::setTitle, dto.getTitle());
        setIfNonNull(event::setAnnotation, dto.getAnnotation());
        setIfNonNull(event::setDescription, dto.getDescription());
        setIfNonNull(event::setEventDate, dto.getEventDate());
        setIfNonNull(event::setPaid, dto.getPaid());
        setIfNonNull(event::setRequestModeration, dto.getRequestModeration());
        setIfNonNull(event::setParticipantLimit, dto.getParticipantLimit());
        setIfNonNull(event::setState, state);
        return event;
    }

    public static Event checkForUser(Event event, UserUpdateEventDto dto, State state) {
        setIfNonNull(event::setTitle, dto.getTitle());
        setIfNonNull(event::setAnnotation, dto.getAnnotation());
        setIfNonNull(event::setDescription, dto.getDescription());
        setIfNonNull(event::setEventDate, dto.getEventDate());
        setIfNonNull(event::setPaid, dto.getPaid());
        setIfNonNull(event::setRequestModeration, dto.getRequestModeration());
        setIfNonNull(event::setParticipantLimit, dto.getParticipantLimit());
        setIfNonNull(event::setState, state);
        return event;
    }
}
