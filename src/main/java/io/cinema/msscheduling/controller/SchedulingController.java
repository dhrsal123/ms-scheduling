package io.cinema.msscheduling.controller;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMoviesResponseDTO;
import io.cinema.msscheduling.service.SchedulingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theaters/{theaterId}/schedule-movies/{branchId}")
public class SchedulingController {
    private final SchedulingService schedulingService;

    @GetMapping
    public Flux<ScheduledMoviesResponseDTO> getScheduledMovies(
            @PathVariable UUID theaterId,
            @PathVariable UUID branchId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return schedulingService.getScheduledMovies(theaterId, branchId, page, size);
    }

    @PostMapping
    public Mono<ScheduledMovieResponseDTO> saveScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID branchId,
            @Valid @RequestBody ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return schedulingService.saveScheduleMovie(theaterId, branchId, scheduleMovieRequest);
    }

    @PutMapping("/{scheduleId}")
    public Mono<ScheduledMovieResponseDTO> updateScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID branchId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return schedulingService.updateScheduleMovie(theaterId, branchId, scheduleId, scheduleMovieRequest);
    }

    @PutMapping("/{scheduleId}")
    public Mono<Void> deleteScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID branchId,
            @PathVariable UUID scheduleId
    ) {
        return schedulingService.deleteScheduleMovie(theaterId, branchId, scheduleId);
    }


}
