package ru.practicum.explore.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "id", ignore = true)
    EndpointHit toEndpointHit(EndpointHitDto dto);

    OutEndpointHitDto toOutEndpointHitDto(EndpointHit endpointHit);
}
