package edu.java.clientGithub;

import edu.java.retry.BackoffType;
import edu.java.retry.RetryGenerator;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


public class GitHubWebClient implements GithubClient {

    private static final String DEFAULT_URL = "https://api.github.com";
    private final WebClient webClient;
    private Retry retry;

    @Value(value = "${api.github.backOffType}")
    private BackoffType backoffType;

    @Value(value = "${api.github.retryCount}")
    private int retryCount;

    @Value(value = "${api.github.retryInterval}")
    private int retryInterval;

    @Value(value = "${api.github.statuses}")
    private List<HttpStatus> statuses;

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

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses,
            "github-client"
        );
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

    @Override
    public Flux<GithubResponse> retryFetchRepositoryInfo(String owner, String repoName) {
        return Retry.decorateSupplier(retry, () -> fetchRepositoryInfo(owner, repoName)).get();
    }
}
