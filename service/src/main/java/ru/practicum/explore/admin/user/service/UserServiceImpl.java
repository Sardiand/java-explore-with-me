package ru.practicum.explore.admin.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.user.dto.InUserDto;
import ru.practicum.explore.admin.user.dto.OutUserDto;
import ru.practicum.explore.admin.user.dto.mapper.UserMapper;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.admin.user.repository.UserRepository;
import ru.practicum.explore.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public OutUserDto add(InUserDto dto) {
        User user = mapper.toUser(dto);
        User saved = repository.save(user);
        log.debug("User saved {}", saved.getId());
        return mapper.toOutUserDto(saved);
    }

    @Override
    public List<OutUserDto> getAll(int from, int size, List<Long> ids) {
        Pageable sortedById = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id"));
        List<User> users = new ArrayList<>();
        if (ids == null) {
            users.addAll(repository.findAll(sortedById).getContent());
        } else {
            users.addAll(repository.findAllByIdIn(ids, sortedById).getContent());
        }
        List<OutUserDto> outUsers = new ArrayList<>();
        if (!users.isEmpty()) {
            for (User user : users) {
                outUsers.add(mapper.toOutUserDto(user));
            }
        }
        return outUsers;
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        repository.deleteById(userId);
    }
}
