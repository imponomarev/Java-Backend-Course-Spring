package edu.java.api.model;

import org.jetbrains.annotations.NotNull;
import java.net.URI;

public record AddLinkRequest(
    @NotNull URI link
) {
}
