package ru.practicum.explore.privateApi.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.admin.user.repository.UserRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.State;
import ru.practicum.explore.privateApi.event.repository.EventRepository;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.model.Request;
import ru.practicum.explore.privateApi.request.model.RequestStatus;
import ru.practicum.explore.privateApi.request.dto.mapper.RequestMapper;
import ru.practicum.explore.privateApi.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final RequestMapper mapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestPartDto create(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event not found %d", eventId)));
        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatusOrderById(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests.size());
        checkEvent(event, userId);
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User not found %d", userId)));
        RequestStatus status;
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            status = RequestStatus.PENDING;
        }
        Request request = Request.builder()
                .requester(requester)
                .status(status)
                .event(event)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        eventRepository.save(event);
        Request savedRequest = requestRepository.save(request);
        return mapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public RequestPartDto delete(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request not found %d", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        return mapper.toDto(savedRequest);
    }

    @Override
    public List<RequestPartDto> getAll(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkEvent(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Initiator of event cannot send request to participate");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Request can be send only for published events");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new IllegalArgumentException("Request can be send only for events available to participate");
        }
    }
}
