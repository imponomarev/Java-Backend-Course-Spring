package edu.java.updaters;

import edu.java.clientStackOverflow.StackOverflowClient;
import edu.java.clientStackOverflow.StackOverflowResponse;
import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.services.LinkService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackOverflowUpdater implements LinkUpdater {

    private final StackOverflowClient stackOverflowWebClient;
    private final LinkService linkService;
    private static final String HOST = "stackoverflow.com";

    @Override
    public String update(LinkDto linkDto) {

        LinkDto link = linkDto;

        long questionId = Long.parseLong(link.url().getPath().split("/")[2]);

        StackOverflowResponse response = stackOverflowWebClient.retryFetchQuestion(questionId).blockFirst();

        OffsetDateTime lastCheck = OffsetDateTime.now();
        if (!link.lastUpdate().equals(response.lastActivityDate())) {
            link = new LinkDto(
                link.id(),
                link.url(),
                response.lastActivityDate(),
                lastCheck
            );
            linkService.update(link);
            return getResponseDescription(response);
        } else {
            link = new LinkDto(
                link.id(),
                link.url(),
                link.lastUpdate(),
                lastCheck
            );
            linkService.update(link);
            return null;
        }
    }

    @Override
    public String getHost() {
        return HOST;
    }

    private String getResponseDescription(StackOverflowResponse response) {
        return "The answer came to question " + response.questionId()
            + " on StackOverflow by " + response.owner().displayName()
            + " with reputation: " + response.owner().reputation();
    }

}
