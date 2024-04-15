package edu.java.domain.jdbc.dto;

import java.time.OffsetDateTime;

public record ChatDto(
    Long id,
    OffsetDateTime createdAt
) {
}
