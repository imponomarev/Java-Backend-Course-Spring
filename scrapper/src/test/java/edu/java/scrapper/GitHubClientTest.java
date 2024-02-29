package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.clientGithub.GitHubWebClient;
import edu.java.clientGithub.GithubClient;
import edu.java.clientGithub.GithubResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.OffsetDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GitHubClientTest {

   private WireMockServer wireMockServer;
   private GithubClient githubClient;

   @BeforeEach
    void init() {
       wireMockServer = new WireMockServer();
       wireMockServer.start();
       WireMock.configureFor("localhost", wireMockServer.port());
       githubClient = new GitHubWebClient("http://localhost:"
       + wireMockServer.port());
   }

   @AfterEach
    void stop() {
       wireMockServer.stop();
   }

    @Test
    void fetchRepositoryInfoTestCorrectWorking() {
        String ownerName = "imponomarev";
        String repositoryName = "Java-Backend-Course-Spring-2024";
        String responseBody = """
            [
              {
                "id": "35920657560",
                "type": "PushEvent",
                "actor": {
                  "id": 91834253,
                  "login": "imponomarev",
                  "display_login": "imponomarev",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/imponomarev",
                  "avatar_url": "https://avatars.githubusercontent.com/u/91834253?"
                },
                "repo": {
                  "id": 759522124,
                  "name": "imponomarev/Java-Backend-Course-Spring-2024",
                  "url": "https://api.github.com/repos/imponomarev/Java-Backend-Course-Spring-2024"
                },
                "payload": {
                  "repository_id": 759522124,
                  "push_id": 17218524847,
                  "size": 1,
                  "distinct_size": 1,
                  "ref": "refs/heads/hw1",
                  "head": "370c2f652aaaaffe74a068a0fa1957cd0bdde6da",
                  "before": "b225c4f736ccf7c1ae16ad969c3778765cadc242",
                  "commits": [
                    {
                      "sha": "370c2f652aaaaffe74a068a0fa1957cd0bdde6da",
                      "author": {
                        "email": "i.ponomarev.1991@gmail.com",
                        "name": "Ponomarev Ivan"
                      },
                      "message": "changes after review#2",
                      "distinct": true,
                      "url": "https://api.github.com/repos/imponomarev/Java-Backend-Course-Spring-2024/commits/370c2f652aaaaffe74a068a0fa1957cd0bdde6da"
                    }
                  ]
                },
                "public": true,
                "created_at": "2024-02-22T18:02:52Z"
              }
            ]
            """;

        Long expectedId = 35920657560L;
        String expectedType = "PushEvent";
        String expectedActorLogin = "imponomarev";
        String expectedActorUrl = "https://api.github.com/users/imponomarev";
        String expectedRepositoryName = "imponomarev/Java-Backend-Course-Spring-2024";
        String expectedRepositoryUrl = "https://api.github.com/repos/imponomarev/Java-Backend-Course-Spring-2024";
        OffsetDateTime expectedCreatedAt = OffsetDateTime.parse("2024-02-22T18:02:52Z");

        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner" , ownerName,
                "repo", repositoryName
            ));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Flux<GithubResponse> response = githubClient.fetchRepositoryInfo(ownerName, repositoryName);

        StepVerifier.create(response)
            .consumeNextWith(resp -> {
                assertNotNull(resp);
                assertEquals(expectedId, resp.id());
                assertEquals(expectedType, resp.type());
                assertEquals(expectedActorLogin, resp.actor().login());
                assertEquals(expectedActorUrl, resp.actor().url());
                assertEquals(expectedRepositoryName, resp.repo().name());
                assertEquals(expectedRepositoryUrl, resp.repo().url());
                assertEquals(expectedCreatedAt, resp.createdAt());

            })
            .verifyComplete();
    }

    @Test
    void fetchRepositoryInfoTestWithEmptyResponse() {
        String ownerName = "imponomarev";
        String repositoryName = "Java-Backend-Course-Spring-2024";
        String responseBody = "[]";

        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner" , ownerName,
                "repo", repositoryName
            ));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Flux<GithubResponse> response = githubClient.fetchRepositoryInfo(ownerName, repositoryName);

        StepVerifier.create(response)
            .expectComplete()
            .verify();
    }

    @Test
    void fetchRepositoryInfoTestWithWrongRespBody() {
        String ownerName = "imponomarev";
        String repositoryName = "Java-Backend-Course-Spring-2024";
        String responseBody = "Hello world!";

        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner" , ownerName,
                "repo", repositoryName
            ));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Flux<GithubResponse> response = githubClient.fetchRepositoryInfo(ownerName, repositoryName);

        StepVerifier.create(response)
            .expectError(DecodingException.class)
            .verify();
    }
}

