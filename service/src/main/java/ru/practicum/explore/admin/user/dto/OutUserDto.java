package ru.practicum.explore.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OutUserDto {
    private Long id;
    private String name;
    private String email;
}
