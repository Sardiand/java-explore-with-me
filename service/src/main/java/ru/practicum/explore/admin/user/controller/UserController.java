package ru.practicum.explore.admin.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.user.dto.InUserDto;
import ru.practicum.explore.admin.user.dto.OutUserDto;
import ru.practicum.explore.admin.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutUserDto add(@RequestBody @Valid InUserDto dto) {
        return service.add(dto);
    }
}
