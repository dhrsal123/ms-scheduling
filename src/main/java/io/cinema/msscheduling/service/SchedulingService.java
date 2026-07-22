package io.cinema.msscheduling.service;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SchedulingService {

    Flux<ScheduledMovieResponseDTO> getScheduledMovies(
            UUID theaterId,
            UUID roomId,
            int page,
            int size
    );

    Mono<ScheduledMovieResponseDTO> saveScheduleMovie(
            UUID theaterId,
            UUID roomId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    );

    Mono<ScheduledMovieResponseDTO> updateScheduleMovie(
            UUID theaterId,
            UUID roomId,
            UUID scheduleId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    );

    Mono<Void> deleteScheduleMovie(
            UUID theaterId,
            UUID roomId,
            UUID scheduleId
    );
}
