package io.cinema.msscheduling.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("scheduled_movie")
public class ScheduledMovieEntity extends AuditableEntity {
    @Id
    private UUID id;

    private UUID theaterId;

    private UUID roomId;

    private UUID movieId;

    private LocalDateTime start;

    private LocalDateTime end;
}
