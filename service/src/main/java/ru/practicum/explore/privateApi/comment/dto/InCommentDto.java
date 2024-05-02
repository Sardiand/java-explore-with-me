package ru.practicum.explore.privateApi.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InCommentDto {
    @NotBlank
    @Size(max = 255)
    private String text;
}
