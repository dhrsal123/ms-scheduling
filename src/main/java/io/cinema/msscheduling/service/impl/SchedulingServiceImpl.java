package io.cinema.msscheduling.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msscheduling.config.SchedulingProperties;
import io.cinema.msscheduling.domain.dto.request.MoviesBatchRequestDTO;
import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.MovieInfoResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import io.cinema.msscheduling.mapper.ScheduledMovieMapper;
import io.cinema.msscheduling.repository.ScheduledMoviesRepository;
import io.cinema.msscheduling.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BUSINESS_ERROR;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {

    private static final String MOVIES_BASE_URL = "api/v1/movies/";
    private static final String THEATER_MANAGEMENT_BASE_URL = "api/v1/theaters/%s/rooms/%s";
    private static final String OFFSET_ID = "-05:00";
    private final ScheduledMoviesRepository scheduledMoviesRepository;
    private final TransactionalOperator transactionalOperator;
    private final ScheduledMovieMapper scheduledMovieMapper;

    private final SchedulingProperties schedulingProperties;
    private final WebClient webClient;

    @Override
    public Flux<ScheduledMovieResponseDTO> getScheduledMovies(UUID theaterId, UUID roomId, int page, int size) {
        var now = LocalDateTime.now(ZoneOffset.of(OFFSET_ID));
        return verifyRoomBelongsToTheater(theaterId, roomId)
                .thenMany(Flux.defer(() ->
                        scheduledMoviesRepository.findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                                        now,
                                        theaterId,
                                        roomId,
                                        PageRequest.of(page, size)
                                )
                                .collectList()
                                .as(transactionalOperator::transactional)
                                .doOnError(e ->
                                        log.error("Failed to find all scheduledMovies, error: {} ", e.getMessage())
                                )
                                .onErrorMap(
                                        e -> !(e instanceof CinemaException),
                                        e -> new CinemaException(
                                                "Unexpected system error during read",
                                                TECHNICAL_ERROR
                                        )
                                )
                                .flatMapMany(this::enrichMoviesBatch)
                ));
    }

    @Override
    public Mono<ScheduledMovieResponseDTO> saveScheduleMovie(
            UUID theaterId,
            UUID roomId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return verifyRoomBelongsToTheater(theaterId, roomId)
                .then(Mono.defer(() -> {
                    var entity = scheduledMovieMapper.toEntity(scheduleMovieRequest, theaterId, roomId);
                    return scheduledMoviesRepository.save(entity)
                            .as(transactionalOperator::transactional)
                            .doOnError(e ->
                                    log.error("Failed to save the schedule for the movie, error: {} ", e.getMessage())
                            )
                            .onErrorMap(
                                    e -> !(e instanceof CinemaException),
                                    e -> new CinemaException(
                                            "Unexpected system error during save",
                                            TECHNICAL_ERROR
                                    )
                            )
                            .flatMap(this::enrichMovieResponse);
                }));
    }

    @Override
    public Mono<ScheduledMovieResponseDTO> updateScheduleMovie(
            UUID theaterId,
            UUID roomId,
            UUID scheduleId,
            ScheduleMovieRequestDTO scheduleMovieRequest
    ) {
        return verifyRoomBelongsToTheater(theaterId, roomId)
                .then(Mono.defer(() -> scheduledMoviesRepository.findById(scheduleId)
                        .switchIfEmpty(Mono.error(new CinemaException("Schedule not found", BUSINESS_ERROR)))
                        .flatMap(savedEntity -> {
                            var updatedEntity = scheduledMovieMapper
                                    .partialUpdate(savedEntity, scheduleMovieRequest, theaterId, roomId);
                            return scheduledMoviesRepository.save(updatedEntity);
                        })
                        .as(transactionalOperator::transactional)
                        .doOnError(e -> log.error("Failed update the schedule, error: {} ", e.getMessage()))
                        .onErrorMap(
                                e -> !(e instanceof CinemaException),
                                e -> new CinemaException("Unexpected system error during update", TECHNICAL_ERROR)
                        )
                        .flatMap(this::enrichMovieResponse)
                ));
    }

    @Override
    public Mono<Void> deleteScheduleMovie(
            UUID theaterId,
            UUID roomId,
            UUID scheduleId
    ) {
        return verifyRoomBelongsToTheater(theaterId, roomId)
                .then(Mono.defer(() -> scheduledMoviesRepository.findById(scheduleId)
                        .switchIfEmpty(Mono.error(new CinemaException("Schedule not found", BUSINESS_ERROR)))
                        .flatMap(savedEntity -> scheduledMoviesRepository.deleteById(scheduleId))
                        .as(transactionalOperator::transactional)
                        .doOnError(e -> log.error("Failed delete the schedule, error: {} ", e.getMessage()))
                        .onErrorMap(
                                e -> !(e instanceof CinemaException),
                                e -> new CinemaException("Unexpected system error during delete", TECHNICAL_ERROR)
                        )
                ));

    }

    private Mono<ScheduledMovieResponseDTO> enrichMovieResponse(ScheduledMovieEntity savedEntity) {
        var moviesServiceUrl = buildMoviesHost() + savedEntity.getMovieId();

        return webClient.get()
                .uri(moviesServiceUrl)
                .retrieve()
                .bodyToMono(MovieInfoResponseDTO.class)
                .map(responseDto -> scheduledMovieMapper.toDto(savedEntity, responseDto));
    }

    private Flux<ScheduledMovieResponseDTO> enrichMoviesBatch(List<ScheduledMovieEntity> entities) {
        if (entities.isEmpty()) {
            return Flux.empty();
        }

        var movieIds = entities.stream()
                .map(ScheduledMovieEntity::getMovieId)
                .collect(Collectors.toSet());

        var moviesBatchUrl = buildMoviesHost() + "batch";

        var batchRequest = new MoviesBatchRequestDTO(movieIds);

        return webClient.post()
                .uri(moviesBatchUrl)
                .bodyValue(batchRequest)
                .retrieve()
                .bodyToFlux(MovieInfoResponseDTO.class)
                .collectMap(MovieInfoResponseDTO::id)
                .flatMapMany(movieInfoMap -> Flux.fromIterable(entities)
                        .filter(entity ->
                                entity.getMovieId() != null &&
                                        movieInfoMap.containsKey(entity.getMovieId()))
                        .map(entity ->
                                scheduledMovieMapper
                                        .toDto(
                                                entity,
                                                movieInfoMap.get(entity.getMovieId())
                                        )
                        )
                );
    }

    private String buildMoviesHost() {
        var moviesHost = schedulingProperties.getMoviesHost();
        var moviesPort = schedulingProperties.getMoviesPort();
        return String.format("%s:%s/%s", moviesHost, moviesPort, MOVIES_BASE_URL);
    }

    private Mono<Void> verifyRoomBelongsToTheater(UUID theaterId, UUID roomId) {
        var theaterManagementUrl = buildTheaterManagementHost(theaterId, roomId);

        return webClient.get()
                .uri(theaterManagementUrl)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(
                        new CinemaException("Theater management service responded with an error", TECHNICAL_ERROR)
                ))
                .toBodilessEntity()
                .then();
    }

    private String buildTheaterManagementHost(UUID theaterId, UUID roomId) {
        var theaterManagementHost = schedulingProperties.getTheaterManagementHost();
        var theaterManagementPort = schedulingProperties.getTheaterManagementPort();
        return String.format("%s:%s/" + THEATER_MANAGEMENT_BASE_URL,
                theaterManagementHost,
                theaterManagementPort,
                theaterId,
                roomId
        );
    }
}
