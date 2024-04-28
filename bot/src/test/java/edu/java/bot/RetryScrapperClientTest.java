package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.api.model.RemoveLinkRequest;
import edu.java.bot.client.ScrapperClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.spy;

@SpringBootTest
public class RetryScrapperClientTest {

    private WireMockServer wireMockServer;

    @Autowired
    private ScrapperClient scrapperClient;
    private ScrapperClient client;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        client = spy(scrapperClient);
    }

    @AfterEach
    void stop() {
        wireMockServer.stop();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void RetryRegisterMethodTest(int httpCode) {

        Long id = 123L;

        wireMockServer.stubFor(WireMock.post(urlEqualTo("/tg-chat/123"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable exception = catchThrowable(() -> client.retryRegisterChat(id));

        Mockito.verify(client, Mockito.times(3)).registerChat(id);

        Assertions.assertInstanceOf(WebClientResponseException.class, exception);

    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void RetryDeleteMethodTest(int httpCode) {

        Long id = 123L;

        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/tg-chat/123"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable exception = catchThrowable(() -> client.retryDeleteChat(id));

        Mockito.verify(client, Mockito.times(3)).deleteChat(id);

        Assertions.assertInstanceOf(WebClientResponseException.class, exception);

    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void RetryGetLinksMethodTest(int httpCode) {

        Long id = 123L;

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("123"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable exception = catchThrowable(() -> client.retryGetLinks(id));

        Mockito.verify(client, Mockito.times(3)).getLinks(id);

        Assertions.assertInstanceOf(WebClientResponseException.class, exception);

    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForDeleteLink(int httpCode) {

        Long id = 123L;

        RemoveLinkRequest request =
            new RemoveLinkRequest(URI.create("https://github.com/imponomarev/Java-Backend-Course-Spring-2024"));

        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("123"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable exception = catchThrowable(() -> client.retryRemoveLink(id, request));

        Mockito.verify(client, Mockito.times(3)).removeLink(id, request);

        Assertions.assertInstanceOf(WebClientResponseException.class, exception);
    }

//    @ParameterizedTest
//    @ValueSource(ints = {500, 502, 507, 503, 504})
//    public void RetryAddLinkMethodTest(int httpCode) {
//
//        Long id = 123L;
//
//        AddLinkRequest request =
//            new AddLinkRequest(URI.create("https://github.com/imponomarev/Java-Backend-Course-Spring-2024"));
//
//        wireMockServer.stubFor(WireMock.post(urlEqualTo("/links"))
//            .withHeader("Tg-Chat-Id", WireMock.equalTo("123"))
//            .willReturn(WireMock.aResponse()
//                .withStatus(httpCode))
//        );
//
//        Throwable exception = catchThrowable(() -> client.retryAddLink(id, request));
//
//        Mockito.verify(client, Mockito.times(1)).addLink(id, request);
//
//        Assertions.assertInstanceOf(WebClientResponseException.class, exception);
//
//    }

}
