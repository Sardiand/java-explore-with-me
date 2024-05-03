package ru.practicum.explore.publicAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.StatClient;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.category.dto.mapper.CategoryMapper;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.category.repository.CategoryRepository;
import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.admin.compilation.dto.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilation.model.Compilation;
import ru.practicum.explore.admin.compilation.repository.CompilationRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.model.EndpointHitDto;
import ru.practicum.explore.model.ViewStats;
import ru.practicum.explore.privateApi.comment.dto.CommentDto;
import ru.practicum.explore.privateApi.comment.dto.mapper.CommentMapper;
import ru.practicum.explore.privateApi.comment.model.Comment;
import ru.practicum.explore.privateApi.comment.repository.CommentRepository;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.privateApi.event.dto.mapper.EventMapper;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.State;
import ru.practicum.explore.privateApi.event.repository.EventRepository;
import ru.practicum.explore.privateApi.request.model.Request;
import ru.practicum.explore.privateApi.request.model.RequestStatus;
import ru.practicum.explore.privateApi.request.repository.RequestRepository;
import ru.practicum.explore.publicAPI.event.EventSearchingParams;
import ru.practicum.explore.specification.EventSpec;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicApiServiceImpl implements PublicApiService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatClient statClient;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CompilationDto getCompById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation not found %d", compId)));
        if (compilation.getEvents().isEmpty()) {
            return compilationMapper.toCompilationDto(compilation, new ArrayList<>());
        }
        List<ShortEventDto> eventDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
        return compilationMapper.toCompilationDto(compilation, eventDtos);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        List<Compilation> compilations = new ArrayList<>();
        if (pinned != null) {
            compilations.addAll(compilationRepository.findAllByPinned(pinned, sortedById));
        } else {
            compilations.addAll(compilationRepository.findAll(sortedById).getContent());
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        return compilations.stream()
                .map(compilation -> compilationMapper.toCompilationDto(compilation, compilation.getEvents().stream()
                        .map(eventMapper::toShortEventDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public OutCategoryDto getCatById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category not found %d", catId)));
        return categoryMapper.toOutCategoryDto(category);
    }

    @Override
    public List<OutCategoryDto> getCategories(int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        List<Category> categories = categoryRepository.findAll(sortedById).getContent();
        return categories.stream()
                .map(categoryMapper::toOutCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto getEventById(Long id, HttpServletRequest request) {
        saveStats(request);
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", id)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Event must be published %d", id));
        }
        List<Request> confirmedRequests = requestRepository
                .findAllByEventIdAndStatusOrderById(id, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests.size());
        LocalDateTime startDateTime = LocalDateTime.now().minusYears(100).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(5).truncatedTo(ChronoUnit.SECONDS);
        ResponseEntity<List<ViewStats>> response = statClient.get(startDateTime,
                endDateTime, true, List.of("/events/" + id));
        if (response.getBody() != null && response.getBody().size() != 0) {
            Long hits = response.getBody().get(0).getHits();
            event.setViews(hits);
        }
        eventRepository.save(event);
        return eventMapper.toEventDto(event);
    }

    @Override
    @Transactional
    public List<ShortEventDto> getEvents(EventSearchingParams params, Integer from, Integer size,
                                         HttpServletRequest httpServletRequest) {
        saveStats(httpServletRequest);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        if (params.getSort() != null) {
            switch (params.getSort()) {
                case EVENT_DATE:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
                    break;
                case VIEWS:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
                    break;
            }
        }
        Specification<Event> specs = EventSpec.filterForPublic(params, State.PUBLISHED);
        List<Event> events = eventRepository.findAll(specs, pageable).getContent();
        if (!events.isEmpty()) {
            saveStatsForList(httpServletRequest, events.stream().map(Event::getId).collect(Collectors.toSet()));
            if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
                return events.stream()
                        .map(eventMapper::toShortEventDto)
                        .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                        .collect(Collectors.toList());
            }
            return events.stream()
                    .map(eventMapper::toShortEventDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<CommentDto> getComments(Long eventId, int from, int size) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", eventId)));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        } else {
            return comments.stream().map(commentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }
    }

    private void saveStatsForList(HttpServletRequest request, Set<Long> eventIds) {
        List<EndpointHitDto> dtos = eventIds.stream()
                .map(id -> createEndpointHitDto(request.getRemoteAddr(), "/events/" + id,
                        LocalDateTime.now()))
                .collect(Collectors.toList());
        dtos.forEach(statClient::save);
    }

    private void saveStats(HttpServletRequest request) {
        statClient.save(createEndpointHitDto(request.getRemoteAddr(),
                request.getRequestURI(), LocalDateTime.now()));
        log.info("Stats saved for request URI: {}", request.getRequestURI());
    }

    private EndpointHitDto createEndpointHitDto(String ip, String uri, LocalDateTime timestamp) {
        String artifact = "service";
        return EndpointHitDto.builder()
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp)
                .app(artifact)
                .build();
    }
}
