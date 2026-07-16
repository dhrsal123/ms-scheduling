package io.cinema.msscheduling.domain.dto.request;

import java.util.Set;
import java.util.UUID;

public record MoviesBatchRequestDTO(
        Set<UUID> movieIds
) {
}
