package ru.practicum.explore.admin.user.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.admin.user.dto.InUserDto;
import ru.practicum.explore.admin.user.dto.OutUserDto;
import ru.practicum.explore.admin.user.dto.UserDto;
import ru.practicum.explore.admin.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toUser(InUserDto inUserDto);

    UserDto toUserDto(User user);

    OutUserDto toOutUserDto(User user);
}
