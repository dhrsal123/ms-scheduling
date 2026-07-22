package io.cinema.msscheduling.domain.dto.response;

import java.util.UUID;

public record MovieInfoResponseDTO(
        UUID id,
        String title,
        String posterUrl
) {
}
