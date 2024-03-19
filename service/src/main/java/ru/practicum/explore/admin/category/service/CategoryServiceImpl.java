package ru.practicum.explore.admin.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.category.dto.InCategoryDto;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.category.dto.mapper.CategoryMapper;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.category.repository.CategoryRepository;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository repository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public OutCategoryDto add(InCategoryDto dto) {
        Category category = mapper.toCategory(dto);
        Category saved = repository.save(category);
        log.debug("Category saved {}", category.getId());
        return mapper.toOutCategoryDto(saved);
    }

    @Override
    public OutCategoryDto update(long id, InCategoryDto dto) {
        Category category = checkCategory(id);
        category.setName(dto.getName());
        return mapper.toOutCategoryDto(repository.save(category));
    }

    @Override
    public void delete(long catId) {
        checkCategory(catId);
        List<Event> events = eventRepository.findAllByCategoryId(catId);
        if (events.isEmpty()) {
            repository.deleteById(catId);
        } else {
            throw new ConflictException(String.format("Category contains events and cannot be deleted %d", catId));
        }
    }

    private Category checkCategory(long catId) {
        return repository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория не найдена %d", catId)));
    }

}
