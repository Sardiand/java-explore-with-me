package ru.practicum.explore.privateApi.event.service;

import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.InEventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.privateApi.event.dto.UserUpdateEventDto;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.dto.RequestStatusEventDto;
import ru.practicum.explore.privateApi.request.dto.ResultRequestStatusEventDto;

import java.util.List;

public interface EventService {

    EventDto create(Long userId, InEventDto dto);

    EventDto update(Long userId, Long eventId, UserUpdateEventDto dto);

    ResultRequestStatusEventDto updateByRequest(Long userId, Long eventId, RequestStatusEventDto dto);

    List<ShortEventDto> getAll(Long userId, int from, int size);

    EventDto getById(Long userId, Long eventId);

    List<RequestPartDto> getRequests(Long userId, Long eventId);
}
