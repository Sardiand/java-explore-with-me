package ru.practicum.explore.admin.event.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.explore.admin.event.dto.AdminUpdateEventDto;
import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.privateApi.event.dto.EventDto;


import javax.validation.Valid;
import java.util.List;

@Validated
public interface AdminEventService {
    EventDto update(Long id, AdminUpdateEventDto dto);

    List<EventDto> getAll(@Valid SearchingParams params, Integer from, Integer size);
}
