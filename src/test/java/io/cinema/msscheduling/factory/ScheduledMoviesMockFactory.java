package io.cinema.msscheduling.factory;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.MovieInfoResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

@UtilityClass
public class ScheduledMoviesMockFactory {
    public static ScheduledMovieResponseDTO buildScheduledMovieResponse(UUID movieId) {
        return new ScheduledMovieResponseDTO(
                movieId,
                "Test movie title",
                "https://s3.amazon.com/test-image.jpg",
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 10, 10),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 20, 10, 10)
        );
    }

    public static ScheduleMovieRequestDTO buildScheduleMovieRequestDTO(UUID movieId) {
        return new ScheduleMovieRequestDTO(
                movieId,
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 10, 10),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 20, 10, 10)
        );
    }

    public static ScheduledMovieEntity buildScheduledMovieEntity(UUID theaterId, UUID roomId, UUID movieId) {
        return new ScheduledMovieEntity(
                UUID.randomUUID(),
                theaterId,
                roomId,
                movieId,
                LocalDateTime.of(2026, Month.OCTOBER, 10, 10, 10, 10),
                LocalDateTime.of(2026, Month.OCTOBER, 10, 20, 10, 10)
        );
    }

    public static MovieInfoResponseDTO buildMovieInfoResponseDTO(UUID movieId) {
        return new MovieInfoResponseDTO(
                movieId,
                "Test movie title",
                "https://s3.amazon.com/test-image.jpg"
        );
    }
}
