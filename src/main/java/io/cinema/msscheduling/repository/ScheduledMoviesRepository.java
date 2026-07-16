package io.cinema.msscheduling.repository;

import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ScheduledMoviesRepository extends R2dbcRepository<ScheduledMovieEntity, UUID> {
    Flux<ScheduledMovieEntity> findAllBy(Pageable pageable);

    Flux<ScheduledMovieEntity> findAllByEndGreaterThanEqual(LocalDateTime endIsGreaterThan, Pageable pageable);

    Flux<ScheduledMovieEntity> findAllByEndGreaterThanEqualAndTheaterIdAndRoomId(LocalDateTime endIsGreaterThan,
                                                                                 Pageable pageable,
                                                                                 UUID theaterId,
                                                                                 UUID roomId
    );
}
