package edu.java.clientStackOverflow;

import edu.java.retry.BackoffType;
import edu.java.retry.RetryGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String DEFAULT_URL = "https://api.stackexchange.com/2.3";

    private final WebClient webClient;
    private Retry retry;

    @Value(value = "${api.stackoverflow.backOffType}")
    private BackoffType backoffType;

    @Value(value = "${api.stackoverflow.retryCount}")
    private int retryCount;

    @Value(value = "${api.stackoverflow.retryInterval}")
    private int retryInterval;

    @Value(value = "${api.stackoverflow.statuses}")
    private List<HttpStatus> statuses;

    public StackOverflowWebClient() {
        webClient = WebClient.builder()
            .baseUrl(DEFAULT_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public StackOverflowWebClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses,
            "stackoverflow-client"
        );
    }

    @Override
    public Flux<StackOverflowResponse> fetchQuestion(long questionId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .build(questionId))
            .retrieve()
            .bodyToMono(String.class)
            .flatMapMany(this::parseJsonAndReturnMono);
    }

    @Override
    public Flux<StackOverflowResponse> retryFetchQuestion(long questionId) {
        return Retry.decorateSupplier(retry, () -> fetchQuestion(questionId)).get();
    }

    private Mono<StackOverflowResponse> parseJsonAndReturnMono(String json) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode itemsNode = root.get("items");

            if (itemsNode != null && itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode lastJsonAnswer = itemsNode.get(0);
                StackOverflowResponse response = objectMapper.treeToValue(lastJsonAnswer, StackOverflowResponse.class);
                return Mono.justOrEmpty(response);
            } else {
                return Mono.empty();
            }
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
