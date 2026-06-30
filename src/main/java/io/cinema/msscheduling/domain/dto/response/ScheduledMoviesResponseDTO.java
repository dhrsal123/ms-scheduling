package io.cinema.msscheduling.domain.dto.response;

import java.util.List;

public record ScheduledMoviesResponseDTO (
        List<ScheduledMovieResponseDTO> movies
){

}
