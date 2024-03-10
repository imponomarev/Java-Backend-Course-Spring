package edu.java.bot.api.model;

import java.net.URI;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record LinkUpdateRequest(
    @NotNull
    Long id,
    @NotNull
    URI url,
    @NotNull
    String description,
    @NotNull
    List<Long> tgChatIds
) {
}
