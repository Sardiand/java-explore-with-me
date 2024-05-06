package ru.practicum.explore.admin.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.category.repository.CategoryRepository;
import ru.practicum.explore.admin.event.dto.AdminStateAction;
import ru.practicum.explore.admin.event.dto.AdminUpdateEventDto;
import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.exception.BadRequestException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.mapper.EventMapper;
import ru.practicum.explore.privateApi.event.dto.mapper.LocationMapper;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.Location;
import ru.practicum.explore.privateApi.event.model.State;
import ru.practicum.explore.privateApi.event.repository.EventRepository;
import ru.practicum.explore.privateApi.event.repository.LocationRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore.privateApi.event.util.EventChecker.checkForAdmin;
import static ru.practicum.explore.specification.EventSpec.filterForAdmin;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public EventDto update(Long eventId, AdminUpdateEventDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", eventId)));
        if (!event.getState().equals(State.PENDING)) {
            throw new IllegalArgumentException("Only PENDING events can be updated by admin");
        }
        if (!isDateValid(event, dto)) {
            throw new BadRequestException("Event Date is not valid for publication");
        }
        State state = null;
        if (dto.getStateAction() != null) {
            state = dto.getStateAction().equals(AdminStateAction.REJECT_EVENT) ? State.CANCELED : State.PUBLISHED;
        }
        Event updatedEvent = checkForAdmin(event, dto, state);
        if (updatedEvent.getState().equals(State.PUBLISHED)) {
            updatedEvent.setPublishedOn(LocalDateTime.now());
        }
        if (dto.getCategory() != null && !event.getCategory().getId().equals(dto.getCategory())) {
            Category category = checkCategory(dto.getCategory());
            updatedEvent.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toLocation(dto.getLocation()));
            updatedEvent.setLocation(location);
        }
        Event savedEvent = eventRepository.save(updatedEvent);
        return mapper.toEventDto(savedEvent);
    }

    @Override
    public List<EventDto> getAll(@Valid SearchingParams params, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Specification<Event> specs = filterForAdmin(params);
        List<Event> events = eventRepository.findAll(specs, pageable).getContent();
        if (!events.isEmpty()) {
            return events.stream()
                    .map(mapper::toEventDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Boolean isDateValid(Event event, AdminUpdateEventDto dto) {
        LocalDateTime eventDateWithTimeLapse;
        if (dto.getEventDate() != null) {
            eventDateWithTimeLapse = dto.getEventDate().plusHours(1);
        } else {
            eventDateWithTimeLapse = event.getEventDate().plusHours(1);
        }
        return eventDateWithTimeLapse.isAfter(LocalDateTime.now());
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category not found %d", catId)));
    }
}
