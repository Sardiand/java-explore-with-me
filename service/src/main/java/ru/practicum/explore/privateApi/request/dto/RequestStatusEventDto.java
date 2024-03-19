package ru.practicum.explore.privateApi.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.explore.privateApi.request.model.RequestStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusEventDto {
    private List<Long> requestIds;
    private RequestStatus status;
}
