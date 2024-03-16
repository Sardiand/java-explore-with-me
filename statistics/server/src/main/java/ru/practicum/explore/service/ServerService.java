package ru.practicum.explore.service;

import ru.practicum.explore.model.EndpointHit;
import ru.practicum.explore.model.EndpointHitDto;
import ru.practicum.explore.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerService {
    EndpointHit saveHit(EndpointHitDto dto);

    List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}
