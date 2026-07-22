package io.cinema.msscheduling.domain.annotations;

import io.cinema.msscheduling.service.validator.StartEndTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartEndTimeValidator.class)
public @interface ValidStartEnd {
    String message() default "Invalid start and end time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
