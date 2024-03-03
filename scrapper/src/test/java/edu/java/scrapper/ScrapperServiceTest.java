package edu.java.scrapper;

import edu.java.api.model.LinkResponse;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.service.ScrapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;

class ScrapperServiceTest {
    private ScrapperService scrapperService;

    @BeforeEach
    void setUp() {
        scrapperService = new ScrapperService();
    }

    @Test
    void getLinksTest() throws NotFoundException, BadRequestException {
        scrapperService.registerChat(100L);

        List<LinkResponse> links = scrapperService.getLinks(100L);

        assertThat(links).isNotNull().isEmpty();
    }

    @Test
    void addLinkTest() throws URISyntaxException, BadRequestException, NotFoundException {
        scrapperService.registerChat(100L);

        LinkResponse linkResponse = scrapperService.addLink(100L, new URI("test-url"));

        assertThat(linkResponse).isNotNull();
    }

    @Test
    void twiceRegistrationChatTest() throws BadRequestException {
        scrapperService.registerChat(100L);

        Throwable actualException = catchThrowableOfType(
            () -> scrapperService.registerChat(100L),
            BadRequestException.class
        );

        assertThat(actualException)
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void removeLinkTest() throws URISyntaxException, BadRequestException, NotFoundException {
        scrapperService.registerChat(100L);
        scrapperService.addLink(100L, new URI("test-url"));

        LinkResponse removedLink = scrapperService.removeLink(100L, new URI("test-url"));


        assertThat(removedLink).isNotNull();
        assertThat(removedLink.url()).isEqualTo(new URI("test-url"));
    }
}
