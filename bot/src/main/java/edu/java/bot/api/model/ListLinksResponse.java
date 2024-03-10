package edu.java.bot.api.model;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
}
