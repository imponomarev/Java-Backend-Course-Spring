package edu.java.api.client;

import edu.java.api.model.ApiErrorResponse;
import edu.java.api.model.LinkUpdateRequest;
import edu.java.exceptions.ApiErrorException;
import edu.java.retry.BackoffType;
import edu.java.retry.RetryGenerator;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotClient {

    private static final String DEFAULT_URL = "http://localhost:8090";
    private static final String UPDATES = "/updates";
    private final WebClient webClient;
    private Retry retry;

    @Value(value = "${api.bot.backOffType}")
    private BackoffType backoffType;

    @Value(value = "${api.bot.retryCount}")
    private int retryCount;

    @Value(value = "${api.bot.retryInterval}")
    private int retryInterval;

    @Value(value = "${api.bot.statuses}")
    private List<HttpStatus> statuses;

    public BotClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public BotClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses,
            "bot-client"
        );
    }

    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post()
            .uri(UPDATES)
            .bodyValue(request)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error)))
            )
            .bodyToMono(String.class);
    }

    public Mono<String> retrySendUpdate(LinkUpdateRequest request) {
        return Retry.decorateSupplier(retry, () -> sendUpdate(request)).get();
    }
}
