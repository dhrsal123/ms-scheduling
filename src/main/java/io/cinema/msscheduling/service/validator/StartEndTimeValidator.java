package io.cinema.msscheduling.service.validator;

import io.cinema.msscheduling.domain.annotations.ValidStartEnd;
import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartEndTimeValidator implements ConstraintValidator<ValidStartEnd, ScheduleMovieRequestDTO> {

    @Override
    public boolean isValid(
            ScheduleMovieRequestDTO value,
            ConstraintValidatorContext context
    ) {
        // @NotNull will handle it
        if (value == null || value.start() == null || value.end() == null) {
            return true;
        }

        return value.start().isBefore(value.end());
    }
}
