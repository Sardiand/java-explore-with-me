package ru.practicum.explore.admin.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.event.dto.AdminUpdateEventDto;
import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.admin.event.service.AdminEventService;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.model.State;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService service;

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid AdminUpdateEventDto dto) {
        return service.update(eventId, dto);
    }

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidParameterException("Date range is invalid");
        }
        return service.getAll(new SearchingParams(users, states, categories, rangeStart, rangeEnd), from, size);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public void delete(@PathVariable Long commentId) {
        service.deleteComment(commentId);
    }
}
