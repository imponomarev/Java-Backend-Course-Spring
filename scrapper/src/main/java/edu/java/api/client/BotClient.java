package edu.java.api.client;

import edu.java.api.model.ApiErrorResponse;
import edu.java.api.model.LinkUpdateRequest;
import edu.java.exceptions.ApiErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    private static final String DEFAULT_URL = "http://localhost:8090";
    private static final String UPDATES = "/updates";
    private final WebClient webClient;

    public BotClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public BotClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post()
            .uri(UPDATES)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::equals,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(String.class);
    }
}
