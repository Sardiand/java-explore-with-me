package ru.practicum.explore.admin.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class InCompilationDto {
    private List<Long> events;

    private boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
