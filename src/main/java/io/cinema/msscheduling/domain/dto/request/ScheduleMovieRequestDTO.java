package io.cinema.msscheduling.domain.dto.request;

import io.cinema.msscheduling.domain.annotations.ValidStartEnd;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@ValidStartEnd
public record ScheduleMovieRequestDTO(
        @NotNull(message = "movieId can't be null")
        UUID movieId,

        @NotNull(message = "start can not be null.")
        @FutureOrPresent(message = "start time must be greater than the current date.")
        LocalDateTime start,

        @NotNull(message = "end can not be null.")
        @Future(message = "end time must be greater than the current date.")
        LocalDateTime end
) {
}
