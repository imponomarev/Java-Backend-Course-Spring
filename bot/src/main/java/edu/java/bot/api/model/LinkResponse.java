package edu.java.bot.api.model;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {
}
