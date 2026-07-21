package io.cinema.msscheduling.controller;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.service.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static io.cinema.msscheduling.factory.ScheduledMoviesMockFactory.buildScheduleMovieRequestDTO;
import static io.cinema.msscheduling.factory.ScheduledMoviesMockFactory.buildScheduledMovieResponse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingControllerTest {
    private WebTestClient webTestClient;
    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        this.schedulingService = mock(SchedulingService.class);
        SchedulingController schedulingController = new SchedulingController(schedulingService);

        this.webTestClient = WebTestClient.bindToController(schedulingController).build();
    }

    @Test
    void shouldGetScheduledMovies() {
        // arrange
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var movieId = UUID.randomUUID();

        var scheduledMovies = List.of(buildScheduledMovieResponse(movieId));

        when(schedulingService.getScheduledMovies(theaterId, roomId, 0, 10))
                .thenReturn(Flux.fromIterable(scheduledMovies));

        // act & assert
        webTestClient.get()
                .uri("/api/v1/theaters/{theaterId}/schedule-movies/{roomId}?page=0&size=10", theaterId, roomId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ScheduledMovieResponseDTO.class)
                .contains(scheduledMovies.getFirst())
                .hasSize(1);
    }

    @Test
    void shouldSaveScheduleMovie() {
        // arrange
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var movieId = UUID.randomUUID();

        var scheduledMoviesRequest = buildScheduleMovieRequestDTO(movieId);
        var scheduledMovie = buildScheduledMovieResponse(movieId);

        when(schedulingService.saveScheduleMovie(theaterId, roomId, scheduledMoviesRequest))
                .thenReturn(Mono.just(scheduledMovie));

        // act & assert
        webTestClient.post()
                .uri("/api/v1/theaters/{theaterId}/schedule-movies/{roomId}", theaterId, roomId)
                .body(Mono.just(scheduledMoviesRequest), ScheduleMovieRequestDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ScheduledMovieResponseDTO.class)
                .isEqualTo(scheduledMovie);
    }

    @Test
    void shouldUpdateScheduleMovie() {
        // arrange
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var movieId = UUID.randomUUID();

        var scheduledMoviesRequest = buildScheduleMovieRequestDTO(movieId);
        var scheduledMovie = buildScheduledMovieResponse(movieId);

        var scheduleId = UUID.randomUUID();

        when(schedulingService.updateScheduleMovie(theaterId, roomId, scheduleId, scheduledMoviesRequest))
                .thenReturn(Mono.just(scheduledMovie));

        // act & assert
        webTestClient.put()
                .uri(
                        "/api/v1/theaters/{theaterId}/schedule-movies/{roomId}/{scheduleId}",
                        theaterId,
                        roomId,
                        scheduleId
                )
                .body(Mono.just(scheduledMoviesRequest), ScheduleMovieRequestDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ScheduledMovieResponseDTO.class)
                .isEqualTo(scheduledMovie);
    }

    @Test
    void shouldDeleteScheduleMovie() {
        // arrange
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var scheduleId = UUID.randomUUID();

        when(schedulingService.deleteScheduleMovie(theaterId, roomId, scheduleId)).thenReturn(Mono.empty());

        // act & assert
        webTestClient.delete()
                .uri(
                        "/api/v1/theaters/{theaterId}/schedule-movies/{roomId}/{scheduleId}",
                        theaterId,
                        roomId,
                        scheduleId
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

}