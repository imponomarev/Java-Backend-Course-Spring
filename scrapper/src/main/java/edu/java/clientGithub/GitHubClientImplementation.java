package edu.java.clientGithub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class GitHubClientImplementation implements GithubClient {

    @Value(value = "${api.github.defaultUrl}")
    private String defaultUrl;

    private final WebClient webClient;

    public GitHubClientImplementation() {
        webClient = WebClient.builder()
            .baseUrl(defaultUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public GitHubClientImplementation(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Override
    public Flux<GithubResponse> fetchRepositoryInfo(String owner, String repoName) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/repos/{owner}/{repo}/events")
                .queryParam("per_page", 1)
                .build(owner, repoName))
            .retrieve()
            .bodyToFlux(GithubResponse.class);
    }
}
