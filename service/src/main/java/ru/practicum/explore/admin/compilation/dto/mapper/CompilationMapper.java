package ru.practicum.explore.admin.compilation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.admin.compilation.dto.InCompilationDto;
import ru.practicum.explore.admin.compilation.model.Compilation;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(InCompilationDto dto);

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation, List<ShortEventDto> events);
}
