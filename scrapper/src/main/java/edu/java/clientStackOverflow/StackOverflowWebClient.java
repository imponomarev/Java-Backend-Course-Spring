package edu.java.clientStackOverflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String DEFAULT_URL = "https://api.stackexchange.com/2.3";

    private final WebClient webClient;

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
