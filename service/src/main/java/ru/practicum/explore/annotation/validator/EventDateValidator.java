package ru.practicum.explore.annotation.validator;

import org.springframework.lang.Nullable;
import ru.practicum.explore.annotation.DateValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<DateValidation, LocalDateTime> {

    @Override
    public boolean isValid(@Nullable LocalDateTime date, ConstraintValidatorContext constraintValidatorContext) {
        if (date != null) {
            LocalDateTime dateWithTimeLapse = date.plusHours(2);
            return dateWithTimeLapse.isAfter(LocalDateTime.now());
        }
        return true;
    }
}
