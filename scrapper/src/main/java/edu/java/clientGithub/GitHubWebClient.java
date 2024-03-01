package edu.java.clientGithub;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class GitHubWebClient implements GithubClient {


    private static final String DEFAULT_URL = "https://api.github.com";
    private final WebClient webClient;

    public GitHubWebClient() {
        webClient = WebClient.builder()
            .baseUrl(DEFAULT_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public GitHubWebClient(String baseUrl) {
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
