package edu.java.bot.api.model;

import java.net.URI;
import org.jetbrains.annotations.NotNull;

public record RemoveLinkRequest(
    @NotNull URI link
    ) {
}
