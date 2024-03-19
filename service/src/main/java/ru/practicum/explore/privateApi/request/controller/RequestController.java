package ru.practicum.explore.privateApi.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestPartDto createRequest(@PathVariable Long userId,
                                        @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestPartDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.delete(userId, requestId);
    }

    @GetMapping
    public List<RequestPartDto> getRequests(@PathVariable Long userId) {
        return requestService.getAll(userId);
    }

}
