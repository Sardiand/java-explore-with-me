package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final StatsRepository repository;
    private final StatsMapper mapper;

    @Override
    @Transactional
    public OutEndpointHitDto saveHit(EndpointHitDto dto) {
        EndpointHit hit = mapper.toEndpointHit(dto);
        EndpointHit endpointHit = repository.save(hit);
        return mapper.toOutEndpointHitDto(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (!unique) {
            if (uris == null || uris.isEmpty()) {
                return repository.findAllWithoutUriAndNotUniqueIp(start, end);
            } else {
                return repository.findAllByUriWithNotUniqueIp(uris, start, end);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return repository.findAllWithoutUriAndUniqueIp(start, end);
            } else {
                return repository.findAllByUriWithUniqueIp(uris, start, end);
            }
        }
    }
}
