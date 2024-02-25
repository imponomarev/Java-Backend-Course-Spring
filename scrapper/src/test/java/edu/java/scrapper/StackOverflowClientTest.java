package edu.java.scrapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.clientStackOverflow.StackOverflowClient;
import edu.java.clientStackOverflow.StackOverflowClientImplementation;
import edu.java.clientStackOverflow.StackOverflowResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StackOverflowClientTest {

    private WireMockServer wireMockServer;
    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        stackOverflowClient = new StackOverflowClientImplementation("http://localhost:"
            + wireMockServer.port());
    }

    @AfterEach
    void stop() {
        wireMockServer.stop();
    }

    @Test
    void fetchQuestionTestCorrectWorking() {
        long questionId = 2003505L;
        String responseBody = """
            {
              "items": [
                {
                  "owner": {
                    "account_id": 33990,
                    "reputation": 460591,
                    "user_id": 95592,
                    "user_type": "registered",
                    "accept_rate": 77,
                    "profile_image": "https://i.stack.imgur.com/uMfjc.jpg?s=256&g=1",
                    "display_name": "Matthew Rankin",
                    "link": "https://stackoverflow.com/users/95592/matthew-rankin"
                  },
                  "is_accepted": true,
                  "community_owned_date": 1702663733,
                  "score": 25757,
                  "last_activity_date": 1708727694,
                  "last_edit_date": 1708727694,
                  "creation_date": 1262654035,
                  "answer_id": 2003515,
                  "question_id": 2003505,
                  "content_license": "CC BY-SA 4.0"
                },
                {
                  "owner": {
                    "account_id": 5628953,
                    "reputation": 8477,
                    "user_id": 4456413,
                    "user_type": "registered",
                    "accept_rate": 83,
                    "profile_image": "https://i.stack.imgur.com/8IG5B.jpg?s=256&g=1",
                    "display_name": "Vivek Maru",
                    "link": "https://stackoverflow.com/users/4456413/vivek-maru"
                  },
                  "is_accepted": false,
                  "community_owned_date": 1702663733,
                  "score": 606,
                  "last_activity_date": 1663665397,
                  "last_edit_date": 1663665397,
                  "creation_date": 1512653383,
                  "answer_id": 47696235,
                  "question_id": 2003505,
                  "content_license": "CC BY-SA 4.0"
                }
              ],
              "has_more": true,
              "quota_max": 300,
              "quota_remaining": 299
            }
            """;

        OffsetDateTime expectedLastActivityDate = Instant.ofEpochSecond(1708727694L).atOffset(ZoneOffset.UTC);
        Long expectedQuestionId = 2003505L;
        Long expectedAnswerId = 2003515L;
        String expectedOwnerName = "Matthew Rankin";
        Long expectedOwnerReputation = 460591L;

        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        Flux<StackOverflowResponse> response = stackOverflowClient.fetchQuestion(questionId);

        StepVerifier.create(response)
            .consumeNextWith(resp -> {
                assertNotNull(resp);
                assertEquals(expectedLastActivityDate, resp.lastActivityDate());
                assertEquals(expectedQuestionId, resp.questionId());
                assertEquals(expectedAnswerId, resp.answerId());
                assertEquals(expectedOwnerName, resp.owner().displayName());
                assertEquals(expectedOwnerReputation, resp.owner().reputation());
            })
            .verifyComplete();
    }

    @Test
    void fetchQuestionTestWithEmptyResponse() {
        long questionId = 2003505L;
        String responseBody = "{}";

        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        Flux<StackOverflowResponse> response = stackOverflowClient.fetchQuestion(questionId);

        StepVerifier.create(response)
            .expectComplete()
            .verify();
    }

    @Test
    void fetchQuestionTestWithWrongRespBody() {
        long questionId = 2003505L;
        String responseBody = "{hello world!}";

        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        Flux<StackOverflowResponse> response = stackOverflowClient.fetchQuestion(questionId);

        StepVerifier.create(response)
            .expectError(JsonParseException.class)
            .verify();
    }
}
