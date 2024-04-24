package ru.practicum.explore.privateApi.request.service;

import ru.practicum.explore.privateApi.request.dto.RequestPartDto;

import java.util.List;

public interface RequestService {

    RequestPartDto create(Long userId, Long eventId);

    RequestPartDto delete(Long userId, Long requestId);

    List<RequestPartDto> getAll(Long userId);
}
