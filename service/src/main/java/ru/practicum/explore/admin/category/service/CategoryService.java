package ru.practicum.explore.admin.category.service;

import ru.practicum.explore.admin.category.dto.InCategoryDto;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;

public interface CategoryService {

    OutCategoryDto add(InCategoryDto dto);

    OutCategoryDto update(long id, InCategoryDto dto);

    void delete(long id);
}
