package edu.java.clientStackOverflow;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class StackOverflowClientImplementation implements StackOverflowClient {

    @Value(value = "${api.stackoverflow.defaultUrl}")
    private String defaultUrl;

    private final WebClient webClient;

    public StackOverflowClientImplementation() {
        webClient = WebClient.builder()
            .baseUrl(defaultUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public StackOverflowClientImplementation(String baseUrl) {
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
