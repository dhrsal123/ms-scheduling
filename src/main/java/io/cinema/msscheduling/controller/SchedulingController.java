package io.cinema.msscheduling.controller;

import io.cinema.domain.annotations.HasEmployeeRole;
import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.service.SchedulingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/theaters/{theaterId}/schedule-movies/{roomId}")
public class SchedulingController {

    private static final String CACHE_SCHEDULED_MOVIES = "scheduled_movies";
    private final SchedulingService schedulingService;

    @Cacheable(key = "{#page, #size, #theaterId, #roomId}", value = CACHE_SCHEDULED_MOVIES)
    @GetMapping
    public Flux<ScheduledMovieResponseDTO> getScheduledMovies(
            @PathVariable UUID theaterId,
            @PathVariable UUID roomId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return schedulingService.getScheduledMovies(theaterId, roomId, page, size);
    }

    @CacheEvict(value = CACHE_SCHEDULED_MOVIES, allEntries = true)
    @HasEmployeeRole
    @PostMapping
    public Mono<ScheduledMovieResponseDTO> saveScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID roomId,
            @Valid @RequestBody ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return schedulingService.saveScheduleMovie(theaterId, roomId, scheduleMovieRequest);
    }

    @CacheEvict(value = CACHE_SCHEDULED_MOVIES, allEntries = true)
    @HasEmployeeRole
    @PutMapping("/{scheduleId}")
    public Mono<ScheduledMovieResponseDTO> updateScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID roomId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return schedulingService.updateScheduleMovie(theaterId, roomId, scheduleId, scheduleMovieRequest);
    }

    @CacheEvict(value = CACHE_SCHEDULED_MOVIES, allEntries = true)
    @HasEmployeeRole
    @DeleteMapping("/{scheduleId}")
    public Mono<Void> deleteScheduleMovie(
            @PathVariable UUID theaterId,
            @PathVariable UUID roomId,
            @PathVariable UUID scheduleId
    ) {
        return schedulingService.deleteScheduleMovie(theaterId, roomId, scheduleId);
    }

}
