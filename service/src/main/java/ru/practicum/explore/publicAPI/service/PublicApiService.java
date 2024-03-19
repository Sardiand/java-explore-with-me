package ru.practicum.explore.publicAPI.service;

import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.publicAPI.event.EventSearchingParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicApiService {
    CompilationDto getCompById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    OutCategoryDto getCatById(Long catId);

    List<OutCategoryDto> getCategories(int from, int size);

    EventDto getEventById(Long id, HttpServletRequest request);

    List<ShortEventDto> getEvents(EventSearchingParams params, Integer from, Integer size, HttpServletRequest request);
}
