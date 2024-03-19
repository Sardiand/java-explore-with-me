package ru.practicum.explore.privateApi.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.user.dto.UserDto;
import ru.practicum.explore.privateApi.event.model.Location;
import ru.practicum.explore.privateApi.event.model.State;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventDto {

    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String annotation;
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean paid;
    private Boolean requestModeration;

    private State state;

    private Integer participantLimit;
    private Integer confirmedRequests;
    private Integer views;

    private UserDto initiator;
    private Category category;
    private Location location;
}
