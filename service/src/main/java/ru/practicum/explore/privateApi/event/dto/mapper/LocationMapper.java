package ru.practicum.explore.privateApi.event.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.privateApi.event.dto.LocationDto;
import ru.practicum.explore.privateApi.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto dto);

    LocationDto toLocationDto(Location location);
}
