package ru.practicum.explore.privateApi.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShortEventDto {

    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String annotation;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private Integer views;
    private Integer confirmedRequests;

    private Category category;
    private UserDto initiator;
}
