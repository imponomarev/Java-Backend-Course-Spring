package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.api.client.BotClient;
import edu.java.api.model.LinkUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

class BotClientTest {

    private WireMockServer wireMockServer;
    private BotClient botClient;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        botClient = new BotClient("http://localhost:"
            + wireMockServer.port());
    }

    @AfterEach
    void stop() {
        wireMockServer.stop();
    }

    @Test
    void botClientTestWithCorrectBody() throws URISyntaxException {
        String responseBody = "The update has been processed";

        LinkUpdateRequest request = new LinkUpdateRequest(
            222L,
            new URI("23123"),
            "test",
            List.of(22L)
        );

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Mono<String> response = botClient.sendUpdate(request);

        StepVerifier.create(response)
            .expectNext(responseBody)
            .verifyComplete();
    }
}
