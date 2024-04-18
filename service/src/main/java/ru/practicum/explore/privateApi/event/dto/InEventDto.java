package ru.practicum.explore.privateApi.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explore.annotation.DateValidation;
import ru.practicum.explore.privateApi.event.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class InEventDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @DateValidation
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Boolean paid = false;
    private Boolean requestModeration = true;

    @Min(0)
    private Integer participantLimit = 0;
    @NotNull
    private Location location;
    @NotNull
    @Positive
    private Long category;
}
