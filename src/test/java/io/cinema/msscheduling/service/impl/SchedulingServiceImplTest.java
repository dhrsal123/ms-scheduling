package io.cinema.msscheduling.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msscheduling.config.SchedulingProperties;
import io.cinema.msscheduling.domain.dto.response.MovieInfoResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import io.cinema.msscheduling.factory.ScheduledMoviesMockFactory;
import io.cinema.msscheduling.mapper.ScheduledMovieMapper;
import io.cinema.msscheduling.mapper.ScheduledMovieMapperImpl;
import io.cinema.msscheduling.repository.ScheduledMoviesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BUSINESS_ERROR;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;
import static io.cinema.msscheduling.factory.ScheduledMoviesMockFactory.buildScheduleMovieRequestDTO;
import static io.cinema.msscheduling.factory.ScheduledMoviesMockFactory.buildScheduledMovieEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceImplTest {

    @Mock
    private ScheduledMoviesRepository scheduledMoviesRepository;
    @Mock
    private TransactionalOperator transactionalOperator;
    private final ScheduledMovieMapper scheduledMovieMapper = new ScheduledMovieMapperImpl();
    @Mock
    private SchedulingProperties schedulingProperties;
    @Mock
    private ExchangeFunction exchangeFunction;

    private SchedulingServiceImpl schedulingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(schedulingProperties.getTheaterManagementHost()).thenReturn("https://cinema.io");
        when(schedulingProperties.getTheaterManagementPort()).thenReturn(8080);
        lenient().when(schedulingProperties.getMoviesHost()).thenReturn("https://cinema.io");
        lenient().when(schedulingProperties.getMoviesPort()).thenReturn(8081);

        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
        schedulingService = new SchedulingServiceImpl(
                scheduledMoviesRepository,
                transactionalOperator,
                scheduledMovieMapper,
                schedulingProperties,
                webClient
        );
    }

    @Test
    void shouldGetScheduledMovies() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        int page = 0, size = 10;

        var scheduledMovies = List.of(buildScheduledMovieEntity(theaterId, roomId, movieId));
        when(scheduledMoviesRepository.findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                any(LocalDateTime.class),
                eq(theaterId),
                eq(roomId),
                any(PageRequest.class)
        )).thenReturn(Flux.fromIterable(scheduledMovies));

        // act
        var response = schedulingService.getScheduledMovies(theaterId, roomId, page, size);

        // assert
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(exchangeFunction, times(2)).exchange(any(ClientRequest.class));
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(scheduledMoviesRepository).findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                any(LocalDateTime.class),
                eq(theaterId),
                eq(roomId),
                any(PageRequest.class)
        );
    }

    @Test
    void shouldSaveScheduleMovie(){
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var scheduledMovieEntity = buildScheduledMovieEntity(theaterId, roomId, movieId);
        when(scheduledMoviesRepository.save(any(ScheduledMovieEntity.class)))
                .thenReturn(Mono.just(scheduledMovieEntity));

        var request = buildScheduleMovieRequestDTO(movieId);
        // act
        var response = schedulingService.saveScheduleMovie(theaterId, roomId, request);
        // assert
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(exchangeFunction, times(2)).exchange(any(ClientRequest.class));
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(scheduledMoviesRepository).save(any(ScheduledMovieEntity.class));
    }

    @Test
    void shouldUpdateScheduleMovie(){
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var scheduledMovieEntity = buildScheduledMovieEntity(theaterId, roomId, movieId);

        var scheduleId = UUID.randomUUID();
        when(scheduledMoviesRepository.findById(scheduleId))
                .thenReturn(Mono.just(scheduledMovieEntity));

        when(scheduledMoviesRepository.save(any(ScheduledMovieEntity.class)))
                .thenReturn(Mono.just(scheduledMovieEntity));

        var request = buildScheduleMovieRequestDTO(movieId);

        // act
        var response = schedulingService.updateScheduleMovie(theaterId, roomId, scheduleId, request);

        // assert
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(exchangeFunction, times(2)).exchange(any(ClientRequest.class));
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(scheduledMoviesRepository).save(any(ScheduledMovieEntity.class));
        verify(scheduledMoviesRepository).findById(scheduleId);
    }

    @Test
    void shouldDeleteScheduleMovie(){
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var scheduledMovieEntity = buildScheduledMovieEntity(theaterId, roomId, movieId);

        var scheduleId = UUID.randomUUID();
        when(scheduledMoviesRepository.findById(scheduleId))
                .thenReturn(Mono.just(scheduledMovieEntity));

        when(scheduledMoviesRepository.deleteById(scheduleId))
                .thenReturn(Mono.empty());

        // act
        var response = schedulingService.deleteScheduleMovie(theaterId, roomId, scheduleId);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(exchangeFunction).exchange(any(ClientRequest.class));
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(scheduledMoviesRepository).deleteById(scheduleId);
        verify(scheduledMoviesRepository).findById(scheduleId);
    }


    @Test
    void shouldReturnEmptyWhenNoScheduledMovies() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        when(scheduledMoviesRepository.findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                any(LocalDateTime.class), any(), any(), any(PageRequest.class)))
                .thenReturn(Flux.empty());

        // act
        var response = schedulingService.getScheduledMovies(theaterId, roomId, 0, 10);

        // assert
        StepVerifier.create(response)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenTheaterManagementReturnsError() {
        // arrange
        mockWebClientCallsWithTheaterError();

        // act
        Flux<ScheduledMovieResponseDTO> response = schedulingService.getScheduledMovies(
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                10
        );

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e -> e instanceof CinemaException
                        && ((CinemaException) e).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldFailWhenScheduleNotFoundOnUpdate() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var scheduleId = UUID.randomUUID();
        when(scheduledMoviesRepository.findById(scheduleId))
                .thenReturn(Mono.empty());

        // act
        var response = schedulingService.updateScheduleMovie(
                UUID.randomUUID(), UUID.randomUUID(), scheduleId,
                buildScheduleMovieRequestDTO(movieId));

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e -> e instanceof CinemaException
                        && ((CinemaException) e).getExceptionType() == BUSINESS_ERROR)
                .verify();
    }

    @Test
    void shouldFailWhenScheduleNotFoundOnDelete() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        var scheduleId = UUID.randomUUID();
        when(scheduledMoviesRepository.findById(scheduleId))
                .thenReturn(Mono.empty());

        // act
        var response = schedulingService.deleteScheduleMovie(
                UUID.randomUUID(), UUID.randomUUID(), scheduleId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e -> e instanceof CinemaException
                        && ((CinemaException) e).getExceptionType() == BUSINESS_ERROR)
                .verify();
    }

    @Test
    void shouldFailWhenRepositoryFailsOnGet() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        when(scheduledMoviesRepository.findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                any(LocalDateTime.class), any(), any(), any(PageRequest.class)))
                .thenReturn(Flux.error(new RuntimeException("DB failure")));

        // act
        var response = schedulingService.getScheduledMovies(
                UUID.randomUUID(), UUID.randomUUID(), 0, 10);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e -> e instanceof CinemaException
                        && ((CinemaException) e).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldFailWhenRepositoryFailsOnSave() {
        // arrange
        var movieId = UUID.randomUUID();
        mockWebClientCalls(movieId);

        when(scheduledMoviesRepository.save(any(ScheduledMovieEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("DB failure")));

        // act
        var response = schedulingService.saveScheduleMovie(
                UUID.randomUUID(), UUID.randomUUID(),
                buildScheduleMovieRequestDTO(movieId));

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e -> e instanceof CinemaException
                        && ((CinemaException) e).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldFilterOutMoviesWithoutMatchingInfo() {
        // arrange
        var movieId1 = UUID.randomUUID();
        var movieId2 = UUID.randomUUID();
        var batchResponse = List.of(ScheduledMoviesMockFactory.buildMovieInfoResponseDTO(movieId1));
        mockWebClientCalls(batchResponse);

        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var entity1 = buildScheduledMovieEntity(theaterId, roomId, movieId1);
        var entity2 = buildScheduledMovieEntity(theaterId, roomId, movieId2);

        when(scheduledMoviesRepository.findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(
                any(LocalDateTime.class), eq(theaterId), eq(roomId), any(PageRequest.class)))
                .thenReturn(Flux.fromIterable(List.of(entity1, entity2)));

        // act
        var response = schedulingService.getScheduledMovies(theaterId, roomId, 0, 10);

        // assert
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    // private methods
    private void mockWebClientCalls(UUID movieId) {
        mockWebClientCalls(List.of(ScheduledMoviesMockFactory.buildMovieInfoResponseDTO(movieId)));
    }

    private void mockWebClientCalls(List<MovieInfoResponseDTO> batchResponse) {
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenAnswer(invocation -> {
                    var req = invocation.getArgument(0, ClientRequest.class);
                    var path = req.url().getPath();

                    if (req.method() == HttpMethod.GET && path.contains("/theaters/")) {
                        return Mono.just(ClientResponse.create(HttpStatus.OK).build());
                    }
                    if (req.method() == HttpMethod.POST && path.contains("/movies/batch")) {
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(objectMapper.writeValueAsString(batchResponse))
                                .build());
                    }
                    if (req.method() == HttpMethod.GET && path.contains("/movies")) {
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(objectMapper.writeValueAsString(batchResponse.getFirst()))
                                .build());
                    }
                    return Mono.error(new RuntimeException("Unexpected: " + req.method() + " " + path));
                });
    }

    private void mockWebClientCallsWithTheaterError() {
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenAnswer(invocation -> {
                    var req = invocation.getArgument(0, ClientRequest.class);
                    var path = req.url().getPath();

                    if (req.method() == HttpMethod.GET && path.contains("/theaters/")) {
                        return Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND).build());
                    }
                    return Mono.error(new RuntimeException("Unexpected: " + req.method() + " " + path));
                });
    }
}
