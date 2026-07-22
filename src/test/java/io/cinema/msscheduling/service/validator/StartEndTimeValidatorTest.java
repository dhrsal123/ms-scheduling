package io.cinema.msscheduling.service.validator;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartEndTimeValidatorTest {

    private StartEndTimeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StartEndTimeValidator();
    }

    @Test
    void shouldReturnTrueWhenStartBeforeEnd() {
        var dto = new ScheduleMovieRequestDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 0, 0),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 12, 0, 0)
        );
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void shouldReturnFalseWhenStartAfterEnd() {
        var dto = new ScheduleMovieRequestDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 12, 0, 0),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 0, 0)
        );
        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void shouldReturnFalseWhenStartEqualsEnd() {
        var dto = new ScheduleMovieRequestDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 0, 0),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 0, 0)
        );
        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void shouldReturnTrueWhenDtoIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void shouldReturnTrueWhenStartIsNull() {
        var dto = new ScheduleMovieRequestDTO(
                UUID.randomUUID(),
                null,
                LocalDateTime.of(2026, Month.OCTOBER, 10, 12, 0, 0)
        );
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void shouldReturnTrueWhenEndIsNull() {
        var dto = new ScheduleMovieRequestDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 0, 0),
                null
        );
        assertTrue(validator.isValid(dto, null));
    }
}
