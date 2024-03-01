package edu.java.clientGithub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubResponse(
    Long id,
    String type,
    Actor actor,
    Repo repo,
    @JsonProperty("created_at")
    OffsetDateTime createdAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Actor(
        String login,

        String url
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repo(

        String name,

        String url
    ) {
    }
}
