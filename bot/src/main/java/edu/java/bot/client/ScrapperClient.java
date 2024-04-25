package edu.java.bot.client;

import edu.java.bot.api.model.AddLinkRequest;
import edu.java.bot.api.model.ApiErrorResponse;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.api.model.ListLinksResponse;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.exceptions.ApiErrorException;
import java.util.List;
import java.util.Optional;

import edu.java.bot.retry.BackoffType;
import edu.java.bot.retry.RetryGenerator;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class ScrapperClient {

    private static final String DEFAULT_URL = "http://localhost:8080";
    private static final String CHAT = "tg-chat/{id}";
    private static final String LINKS = "/links";
    private static final String HEADER = "Tg-Chat-Id";
    private static final String ERROR_API_CALL = "Error making API call: {}";
    private final WebClient webClient;
    private Retry retry;

    @Value(value = "${api.scrapper.backOffType}")
    private BackoffType backoffType;

    @Value(value = "${api.scrapper.retryCount}")
    private int retryCount;

    @Value(value = "${api.scrapper.retryInterval}")
    private int retryInterval;

    @Value(value = "${api.scrapper.statuses}")
    private List<HttpStatus> statuses;

    public ScrapperClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public ScrapperClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses,
            "scrapper-client"
        );
    }

    public Optional<String> registerChat(Long id) {
        return webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path(CHAT).build(id))
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiError -> {
                        log.error(ERROR_API_CALL, apiError);
                        return Mono.error(new ApiErrorException(apiError));
                    })
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public Optional<String> retryRegisterChat(Long id) {
        return Retry.decorateSupplier(retry, () -> registerChat(id)).get();
    }

    public Optional<String> deleteChat(Long id) {
        return webClient
            .delete()
            .uri(uriBuilder -> uriBuilder.path(CHAT).build(id))
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                resp -> resp.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiError -> {
                        log.error(ERROR_API_CALL, apiError);
                        return Mono.error(new ApiErrorException(apiError));
                    })
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public Optional<String> retryDeleteChat(Long id) {
        return Retry.decorateSupplier(retry, () -> deleteChat(id)).get();
    }

    public Optional<ListLinksResponse> getLinks(Long id) {
        return webClient
            .get()
            .uri(LINKS)
            .header(HEADER, id.toString())
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiError -> {
                        log.error(ERROR_API_CALL, apiError);
                        return Mono.error(new ApiErrorException(apiError));
                    })
            )
            .bodyToMono(ListLinksResponse.class)
            .blockOptional();
    }

    public Optional<ListLinksResponse> retryGetLinks(Long id) {
        return Retry.decorateSupplier(retry, () -> getLinks(id)).get();
    }

    public Optional<LinkResponse> addLink(Long id, AddLinkRequest request) {

        try {
            return webClient
                .post()
                .uri(LINKS)
                .header(HEADER, id.toString())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    HttpStatusCode::is4xxClientError,
                    response -> response.bodyToMono(ApiErrorResponse.class)
                        .flatMap(apiError -> {

                            log.error(ERROR_API_CALL, apiError);

                            return Mono.error(new ApiErrorException(apiError));
                        })
                )
                .bodyToMono(LinkResponse.class)
                .blockOptional();
        } catch (ApiErrorException e) {
            log.error("API Error: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<LinkResponse> retryAddLink(Long id, AddLinkRequest request) {
        return Retry.decorateSupplier(retry, () -> addLink(id, request)).get();
    }

    public Optional<LinkResponse> removeLink(Long id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS)
            .header(HEADER, id.toString())
            .bodyValue(request)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiError -> {
                        log.error(ERROR_API_CALL, apiError);
                        return Mono.error(new ApiErrorException(apiError));
                    })
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> retryRemoveLink(Long id, RemoveLinkRequest request) {
        return Retry.decorateSupplier(retry, () -> removeLink(id, request)).get();
    }
}
