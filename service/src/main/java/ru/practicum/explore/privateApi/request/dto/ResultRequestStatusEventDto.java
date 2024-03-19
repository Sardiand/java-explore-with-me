package ru.practicum.explore.privateApi.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultRequestStatusEventDto {
    private List<RequestPartDto> confirmedRequests;
    private List<RequestPartDto> rejectedRequests;
}
