package ru.practicum.explore.admin.category.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.admin.category.dto.InCategoryDto;
import ru.practicum.explore.admin.category.dto.OutCategoryDto;
import ru.practicum.explore.admin.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "id", ignore = true)
    Category toCategory(InCategoryDto dto);

    OutCategoryDto toOutCategoryDto(Category category);
}
