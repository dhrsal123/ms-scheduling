package io.cinema.msscheduling.repository;

import io.cinema.msscheduling.domain.dto.response.ScheduledMoviesResponseDTO;
import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ScheduledMoviesRepository extends R2dbcRepository<ScheduledMovieEntity, UUID> {
    Flux<ScheduledMovieEntity> findAllBy(Pageable pageable);
}
