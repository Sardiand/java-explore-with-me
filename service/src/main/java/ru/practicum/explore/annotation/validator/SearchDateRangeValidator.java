package ru.practicum.explore.annotation.validator;

import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.annotation.DateRangeValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SearchDateRangeValidator implements ConstraintValidator<DateRangeValidation, SearchingParams> {
    @Override
    public boolean isValid(SearchingParams params, ConstraintValidatorContext context) {
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            return params.getRangeStart().isBefore(params.getRangeEnd());
        } else {
            return true;
        }
    }
}
