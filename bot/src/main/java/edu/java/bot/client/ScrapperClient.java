package edu.java.bot.client;

import edu.java.bot.api.model.AddLinkRequest;
import edu.java.bot.api.model.ApiErrorResponse;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.api.model.ListLinksResponse;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.exceptions.ApiErrorException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {

    private static final String DEFAULT_URL = "http://localhost:8080";
    private static final String CHAT = "tg-chat/{id}";
    private static final String LINKS = "/links";
    private static final String HEADER = "Tg-Chat-Id";
    private final WebClient webClient;

    public ScrapperClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public ScrapperClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    public Mono<String> registerChat(Long id) {
        return webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path(CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(String.class);
    }

    public Mono<String> deleteChat(Long id) {
        return webClient
            .delete()
            .uri(uriBuilder -> uriBuilder.path(CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> resp.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(String.class);
    }

    public Mono<ListLinksResponse> getLinks(Long id) {
        return webClient
            .get()
            .uri(LINKS)
            .header(HEADER, id.toString())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<LinkResponse> addLink(Long id, AddLinkRequest request) {
        return webClient
            .post()
            .uri(LINKS)
            .header(HEADER, id.toString())
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(Long id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS)
            .header(HEADER, id.toString())
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> resp
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiErrorException(error))))
            .bodyToMono(LinkResponse.class);
    }
}
