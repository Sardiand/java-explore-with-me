package ru.practicum.explore.privateApi.event.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.user.dto.mapper.UserMapper;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.privateApi.event.dto.EventDto;
import ru.practicum.explore.privateApi.event.dto.InEventDto;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.Location;
import ru.practicum.explore.privateApi.event.model.State;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "created", target = "createdOn", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    Event toEvent(InEventDto dto, LocalDateTime created, State state,
                  User initiator, Category category, Location location);

    EventDto toEventDto(Event event);

    ShortEventDto toShortEventDto(Event event);
}
