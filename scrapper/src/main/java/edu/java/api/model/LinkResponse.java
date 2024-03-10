package edu.java.api.model;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {
}
