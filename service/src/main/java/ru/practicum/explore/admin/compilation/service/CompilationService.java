package ru.practicum.explore.admin.compilation.service;

import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.admin.compilation.dto.InCompilationDto;
import ru.practicum.explore.admin.compilation.dto.UpdateInCompilationDto;

public interface CompilationService {

    CompilationDto create(InCompilationDto dto);

    CompilationDto update(Long id, UpdateInCompilationDto dto);

    void delete(Long id);
}
