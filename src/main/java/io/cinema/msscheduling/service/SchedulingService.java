package io.cinema.msscheduling.service;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMoviesResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SchedulingService {

    Flux<ScheduledMoviesResponseDTO> getScheduledMovies(
            UUID theaterId,
            UUID branchId,
            int page,
            int size
    );

    Mono<ScheduledMovieResponseDTO> saveScheduleMovie(
            UUID theaterId,
            UUID branchId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    );

    Mono<ScheduledMovieResponseDTO> updateScheduleMovie(
            UUID theaterId,
            UUID branchId,
            UUID scheduleId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    );

    Mono<Void> deleteScheduleMovie(
            UUID theaterId,
            UUID branchId,
            UUID scheduleId
    );
}
