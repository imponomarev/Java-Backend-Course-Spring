package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.api.model.AddLinkRequest;
import edu.java.bot.api.model.LinkResponse;
import edu.java.bot.api.model.ListLinksResponse;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.client.ScrapperClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class ScrapperClientTest {
    private ScrapperClient scrapperClient;

    private WireMockServer wireMockServer;


    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        scrapperClient = new ScrapperClient("http://localhost:"
            + wireMockServer.port());
    }

    @AfterEach
    void stop() {
        wireMockServer.stop();
    }

    @Test
    void registerChatTest(){
        String responseBody = "chat is registered";

        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/100"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));


        Mono<String> response = scrapperClient.registerChat(100L);

        StepVerifier.create(response)
            .expectNext(responseBody)
            .verifyComplete();
    }

    @Test
    void deleteChatTest() {

        String responseBody = "chat was successfully deleted";
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/100"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Mono<String> response = scrapperClient.deleteChat(100L);

        StepVerifier.create(response)
            .expectNext(responseBody)
            .verifyComplete();
    }

    @Test
    void getLinksTest() {
        String responseBody = """
        {
            "links":[
                {
                    "id":100,
                    "url":"test-url"
                }
            ],
            "size":1
        }
        """;

        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("100"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Mono<ListLinksResponse> response = scrapperClient.getLinks(100L);

        StepVerifier.create(response)
            .consumeNextWith(resp -> {
                assertNotNull(resp);
                assertEquals(1, resp.size());
                assertThat(resp.links())
                    .hasSize(1)
                    .extracting(LinkResponse::id, link -> link.url().getPath())
                    .containsExactly(tuple(100L, "test-url"));

            })
            .verifyComplete();
    }

    @Test
    void addLinkTest() throws URISyntaxException {

        String responseBody = """
            {
                "id":100,
                "url":"test-url"
            }
            """;

        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("100"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Mono<LinkResponse> response = scrapperClient.addLink(
            100L, new AddLinkRequest(new URI("test-url"))
        );



        StepVerifier.create(response)
            .consumeNextWith(resp -> {
                assertNotNull(resp);
                assertEquals(100, resp.id());
                assertEquals("test-url", resp.url().getPath());

            })
            .verifyComplete();
    }

    @Test
    void removeLinkTest() throws URISyntaxException {

        String responseBody = """
            {
                "id":100,
                "url":"test-url"
            }
            """;

        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("100"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));


        Mono<LinkResponse> response = scrapperClient.removeLink(
            100L, new RemoveLinkRequest(new URI("test-url"))
        );

        StepVerifier.create(response)
            .consumeNextWith(resp -> {
                assertNotNull(resp);
                assertEquals(100, resp.id());
                assertEquals("test-url", resp.url().getPath());

            })
            .verifyComplete();
    }
}
