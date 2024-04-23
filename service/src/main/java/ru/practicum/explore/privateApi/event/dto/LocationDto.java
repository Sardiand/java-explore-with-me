package ru.practicum.explore.privateApi.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Long id;
    private Float lat;
    private Float lon;
}
