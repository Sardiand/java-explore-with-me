package ru.practicum.explore.admin.category.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.category.dto.InCategoryDto;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(InCategoryDto dto);

    OutCategoryDto toOutCategoryDto(Category category);
}
