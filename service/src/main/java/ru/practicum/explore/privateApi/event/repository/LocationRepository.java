package ru.practicum.explore.privateApi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.privateApi.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
