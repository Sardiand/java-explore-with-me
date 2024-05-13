package ru.practicum.explore.publicAPI.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.exception.BadRequestException;
import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.publicAPI.event.EventSearchingParams;
import ru.practicum.explore.publicAPI.event.Sort;
import ru.practicum.explore.publicAPI.service.PublicApiService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService service;

    @GetMapping("/categories/{catId}")
    public OutCategoryDto getCatById(@PathVariable Long catId) {
        return service.getCatById(catId);
    }

    @GetMapping("/categories")
    public List<OutCategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompById(@PathVariable Long compId) {
        return service.getCompById(compId);
    }

    @GetMapping("/events/{id}")
    public EventDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        return service.getEventById(id, request);
    }

    @GetMapping("/events")
    public List<ShortEventDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) Sort sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size,
                                         HttpServletRequest httpServletRequest) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Date range is invalid");
        }
        EventSearchingParams params = EventSearchingParams.builder().text(text).rangeStart(rangeStart).rangeEnd(rangeEnd)
                .categories(categories).onlyAvailable(onlyAvailable).sort(sort).paid(paid).build();
        return service.getEvents(params, from, size, httpServletRequest);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getCommentsForEvent(@PathVariable @Positive Long eventId,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return service.getComments(eventId, from, size);
    }
}
