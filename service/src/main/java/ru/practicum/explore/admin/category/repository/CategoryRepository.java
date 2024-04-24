package ru.practicum.explore.admin.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.admin.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
