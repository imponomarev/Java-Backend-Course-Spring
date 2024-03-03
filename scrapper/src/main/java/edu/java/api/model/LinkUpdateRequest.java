package edu.java.api.model;

import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @NotNull Long id,
    @NotNull URI url,
    @NotNull String description,
    @NotNull List<Long> tgChatIds
) {
}
