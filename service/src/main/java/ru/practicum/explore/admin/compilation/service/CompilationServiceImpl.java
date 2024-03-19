package ru.practicum.explore.admin.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.compilation.dto.CompilationDto;
import ru.practicum.explore.admin.compilation.dto.InCompilationDto;
import ru.practicum.explore.admin.compilation.dto.UpdateInCompilationDto;
import ru.practicum.explore.admin.compilation.dto.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilation.model.Compilation;
import ru.practicum.explore.admin.compilation.repository.CompilationRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;
import ru.practicum.explore.privateApi.event.dto.mapper.EventMapper;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService{
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto create(InCompilationDto dto) {
        Compilation compilation = compilationMapper.toCompilation(dto);
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
            if (events.size() == dto.getEvents().size()) {
                compilation.setEvents(events);
            }
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        if (compilation.getEvents() == null) {
            return compilationMapper.toCompilationDto(savedCompilation, new ArrayList<>());
        }
        return compilationMapper.toCompilationDto(savedCompilation, savedCompilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList()));
    }

    @Override
    public CompilationDto update(Long id, UpdateInCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Compilation not found %d", id)));
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
            if (events.size() == dto.getEvents().size()) {
                compilation.getEvents().clear();
                compilation.getEvents().addAll(events);
            }
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        List<ShortEventDto> eventDtos = savedCompilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
        return compilationMapper.toCompilationDto(savedCompilation, eventDtos);
    }

    @Override
    public void delete(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Compilation not found %d", id)));
        compilationRepository.delete(compilation);
    }
}
