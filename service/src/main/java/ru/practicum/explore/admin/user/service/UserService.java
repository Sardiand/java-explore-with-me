package ru.practicum.explore.admin.user.service;

import ru.practicum.explore.admin.user.dto.InUserDto;
import ru.practicum.explore.admin.user.dto.OutUserDto;

import java.util.List;

public interface UserService {

    OutUserDto add(InUserDto dto);

    List<OutUserDto> getAll(int from, int size, List<Long> ids);

    void deleteUser(long userId);
}
