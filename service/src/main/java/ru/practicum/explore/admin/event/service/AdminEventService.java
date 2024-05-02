package ru.practicum.explore.admin.event.service;

import ru.practicum.explore.admin.event.dto.AdminUpdateEventDto;
import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.privateApi.event.dto.EventDto;

import java.util.List;

public interface AdminEventService {

    EventDto update(Long id, AdminUpdateEventDto dto);

    List<EventDto> getAll(SearchingParams params, Integer from, Integer size);

    void deleteComment(Long commentId);
}
