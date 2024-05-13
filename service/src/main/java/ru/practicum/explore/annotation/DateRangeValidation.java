package ru.practicum.explore.annotation;

import ru.practicum.explore.annotation.validator.SearchDateRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SearchDateRangeValidator.class)
@Documented
public @interface DateRangeValidation {
    String message() default "{EndRange can't be before StartRange}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
