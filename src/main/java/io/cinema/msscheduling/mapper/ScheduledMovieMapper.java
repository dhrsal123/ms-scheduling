package io.cinema.msscheduling.mapper;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
import io.cinema.msscheduling.domain.dto.response.MovieInfoResponseDTO;
import io.cinema.msscheduling.domain.dto.response.ScheduledMovieResponseDTO;
import io.cinema.msscheduling.domain.entity.ScheduledMovieEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ScheduledMovieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", source = "theaterId")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "movieId", source = "dto.movieId")
    @Mapping(target = "start", source = "dto.start")
    @Mapping(target = "end", source = "dto.end")
    ScheduledMovieEntity toEntity(
            ScheduleMovieRequestDTO dto,
            UUID theaterId,
            UUID roomId
    );

    @Mapping(target = "movieId", source = "entity.movieId")
    @Mapping(target = "title", source = "movieDto.title")
    @Mapping(target = "posterUrl", source = "movieDto.posterUrl")
    @Mapping(target = "start", source = "entity.start")
    @Mapping(target = "end", source = "entity.end")
    ScheduledMovieResponseDTO toDto(ScheduledMovieEntity entity, MovieInfoResponseDTO movieDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", source = "theaterId")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "movieId", source = "dto.movieId")
    @Mapping(target = "start", source = "dto.start")
    @Mapping(target = "end", source = "dto.end")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    ScheduledMovieEntity partialUpdate(
            @MappingTarget ScheduledMovieEntity scheduledMovieEntity,
            ScheduleMovieRequestDTO dto,
            UUID theaterId,
            UUID roomId
    );
}
