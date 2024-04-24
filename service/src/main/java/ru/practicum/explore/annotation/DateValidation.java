package ru.practicum.explore.annotation;

import ru.practicum.explore.annotation.validator.EventDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface DateValidation {
    String message() default "{DateTime is invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
