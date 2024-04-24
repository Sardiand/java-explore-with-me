package ru.practicum.explore.privateApi.request.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.privateApi.request.dto.RequestPartDto;
import ru.practicum.explore.privateApi.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestPartDto toDto(Request request);
}
