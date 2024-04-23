package ru.practicum.explore.privateApi.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.StatClient;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.category.repository.CategoryRepository;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.admin.user.repository.UserRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.model.ViewStats;
import ru.practicum.explore.privateApi.event.dto.*;
import ru.practicum.explore.privateApi.event.dto.mapper.EventMapper;
import ru.practicum.explore.privateApi.event.dto.mapper.LocationMapper;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.Location;
import ru.practicum.explore.privateApi.event.model.State;
import ru.practicum.explore.privateApi.event.repository.EventRepository;
import ru.practicum.explore.privateApi.event.repository.LocationRepository;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.dto.RequestStatusEventDto;
import ru.practicum.explore.privateApi.request.dto.ResultRequestStatusEventDto;
import ru.practicum.explore.privateApi.request.dto.mapper.RequestMapper;
import ru.practicum.explore.privateApi.request.model.Request;
import ru.practicum.explore.privateApi.request.model.RequestStatus;
import ru.practicum.explore.privateApi.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore.privateApi.event.util.EventChecker.checkForUser;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository catRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventMapper mapper;
    private final RequestMapper requestMapper;
    private final StatClient statClient;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public EventDto create(Long userId, InEventDto dto) {
        User user = checkUser(userId);
        Category category = checkCategory(dto.getCategory());
        Location savedLocation = locationRepository.save(locationMapper.toLocation(dto.getLocation()));
        Event event = mapper.toEvent(dto, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), State.PENDING,
                user, category, savedLocation);
        event.setViews(0L);
        event.setConfirmedRequests(0);
        Event savedEvent = eventRepository.save(event);
        return mapper.toEventDto(savedEvent);
    }

    @Override
    @Transactional
    public EventDto update(Long userId, Long eventId, UserUpdateEventDto dto) {
        Event event = checkEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Published events cannot be changed");
        }
        State state = null;
        if (dto.getStateAction() != null) {
            state = dto.getStateAction().equals(StateAction.CANCEL_REVIEW) ? State.CANCELED : State.PENDING;
        }
        Event updatedEvent = checkForUser(event, dto, state);
        if (dto.getCategory() != null && !event.getCategory().getId().equals(dto.getCategory())) {
            Category category = checkCategory(dto.getCategory());
            updatedEvent.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toLocation(dto.getLocation()));
            updatedEvent.setLocation(location);
        }
        updateEventViewsAndConfirmedRequests(event, eventId);
        Event savedEvent = eventRepository.save(updatedEvent);
        return mapper.toEventDto(savedEvent);
    }

    @Override
    @Transactional
    public ResultRequestStatusEventDto updateByRequest(Long userId, Long eventId, RequestStatusEventDto dto) {
        Event event = checkEvent(eventId);
        updateEventViewsAndConfirmedRequests(event, eventId);
        List<Request> requests = requestRepository.findAllByIdIn(dto.getRequestIds());
        if (!requests.isEmpty()) {
            List<Request> pendingRequests = getPendingRequests(requests);
            List<Request> confirmedRequests = new ArrayList<>();
            List<Request> rejectedRequests = new ArrayList<>();
            List<RequestPartDto> savedConfirmedRequests = new ArrayList<>();
            List<RequestPartDto> savedRejectedRequests = new ArrayList<>();
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                confirmedRequests.addAll(confirmRequests(pendingRequests));
                savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
            }
            long availableToParticipate = event.getParticipantLimit() - event.getConfirmedRequests(); //кол мест для участия
            if (availableToParticipate == 0) {
                throw new IllegalArgumentException(String.format("Participant limit is reached for event %d", eventId));
            }
            switch (dto.getStatus()) {
                case REJECTED:
                    rejectedRequests.addAll(rejectRequests(pendingRequests));
                    savedRejectedRequests.addAll(getSavedRequests(rejectedRequests));
                    break;
                case CONFIRMED:
                    if (pendingRequests.size() <= availableToParticipate) {
                        confirmedRequests.addAll(confirmRequests(pendingRequests));
                        savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
                    } else {
                        confirmedRequests.addAll(confirmRequests(pendingRequests.stream()
                                .limit(availableToParticipate)
                                .collect(Collectors.toList())));
                        savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
                        pendingRequests.removeAll(confirmedRequests);
                        rejectedRequests.addAll(rejectRequests(rejectRequests(pendingRequests)));
                        savedRejectedRequests.addAll(getSavedRequests(rejectedRequests));
                    }
            }
            event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
            eventRepository.save(event);
            return new ResultRequestStatusEventDto(savedConfirmedRequests, savedRejectedRequests);
        }
        throw new NotFoundException(String.format("Requests not found for event %d", eventId));
    }

    @Override
    public List<ShortEventDto> getAll(Long userId, int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, sortedById);
        return events.stream()
                .map(mapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto getById(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        updateEventViewsAndConfirmedRequests(event, eventId);
        eventRepository.save(event);
        return mapper.toEventDto(event);
    }

    @Override
    public List<RequestPartDto> getRequests(Long userId, Long eventId) {
        return requestRepository.findAllById(eventId);
    }

    private List<RequestPartDto> getSavedRequests(List<Request> updatedRequests) {
        List<Request> saved = requestRepository.saveAll(updatedRequests);
        return saved.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Request> getPendingRequests(List<Request> requests) {
        List<Request> pendingRequests = requests.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.PENDING))
                .collect(Collectors.toList());
        if (pendingRequests.isEmpty()) {
            throw new IllegalArgumentException("Only PENDING requests can be updated");
        }
        return pendingRequests;
    }

    private void updateEventViewsAndConfirmedRequests(Event event, Long eventId) {
        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatusOrderById(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests.size());
        LocalDateTime startDateTime = LocalDateTime.now().minusYears(100).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(5).truncatedTo(ChronoUnit.SECONDS);

        ResponseEntity<List<ViewStats>> response = statClient.get(
                startDateTime,
                endDateTime,
                true,
                List.of("/events/" + eventId));
        if (response.getBody() != null && response.getBody().size() != 0) {
            Long hits = response.getBody().get(0).getHits();
            event.setViews(hits);
        }
    }

    private List<Request> confirmRequests(List<Request> pendingRequests) {
        return pendingRequests.stream()
                .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                .collect(Collectors.toList());
    }

    private List<Request> rejectRequests(List<Request> pendingRequests) {
        return pendingRequests.stream()
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User not found %d", userId)));
    }

    private Category checkCategory(Long catId) {
        return catRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category not found %d", catId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", eventId)));
    }
}
