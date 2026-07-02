package io.cinema.msscheduling.mapper;

import io.cinema.msscheduling.domain.dto.request.ScheduleMovieRequestDTO;
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
    @Mapping(target = "branchId", source = "branchId")
    @Mapping(target = "roomId", source = "dto.roomId")
    @Mapping(target = "movieId", source = "dto.movieId")
    @Mapping(target = "start", source = "dto.start")
    @Mapping(target = "end", source = "dto.end")
    ScheduledMovieEntity toEntity(
            ScheduleMovieRequestDTO dto,
            UUID theaterId,
            UUID branchId
    );

    ScheduledMovieResponseDTO toDto(ScheduledMovieEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", source = "theaterId")
    @Mapping(target = "branchId", source = "branchId")
    @Mapping(target = "roomId", source = "dto.roomId")
    @Mapping(target = "movieId", source = "dto.movieId")
    @Mapping(target = "start", source = "dto.start")
    @Mapping(target = "end", source = "dto.end")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    ScheduledMovieEntity partialUpdate(
            @MappingTarget ScheduledMovieEntity scheduledMovieEntity,
            ScheduleMovieRequestDTO dto,
            UUID theaterId,
            UUID branchId
    );
}
