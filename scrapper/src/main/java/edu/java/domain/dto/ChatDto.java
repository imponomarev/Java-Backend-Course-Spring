package edu.java.domain.dto;

import java.time.OffsetDateTime;

public record ChatDto(
    Long id,
    OffsetDateTime createdAt
) {
}
