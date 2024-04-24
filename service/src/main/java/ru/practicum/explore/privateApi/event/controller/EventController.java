package ru.practicum.explore.privateApi.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.InEventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.privateApi.event.dto.UserUpdateEventDto;
import ru.practicum.explore.privateApi.event.service.EventService;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.dto.RequestStatusEventDto;
import ru.practicum.explore.privateApi.request.dto.ResultRequestStatusEventDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId, @RequestBody @Valid InEventDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                @RequestBody @Valid UserUpdateEventDto dto) {
        return service.update(userId, eventId, dto);
    }

    @PatchMapping("/{eventId}/requests")
    public ResultRequestStatusEventDto updateEventRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                          @RequestBody RequestStatusEventDto dto) {
        return service.updateByRequest(userId, eventId, dto);
    }

    @GetMapping
    public List<ShortEventDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestPartDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequests(userId, eventId);
    }
}
