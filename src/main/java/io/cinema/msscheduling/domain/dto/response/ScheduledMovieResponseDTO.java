package io.cinema.msscheduling.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduledMovieResponseDTO(

        UUID movieId,
        String title,
        String posterUrl,

        LocalDateTime start,
        LocalDateTime end
) {
}
