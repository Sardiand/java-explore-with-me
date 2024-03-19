package ru.practicum.explore.admin.compilation.dto;

import lombok.*;
import ru.practicum.explore.privateApi.event.dto.ShortEventDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompilationDto {

    @EqualsAndHashCode.Include
    private Long id;
    private List<ShortEventDto> events;
    private boolean pinned;
    private String title;

    public CompilationDto(Long id, boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = new ArrayList<>();
    }
}
