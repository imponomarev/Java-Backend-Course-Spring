package edu.java.updaters;

import edu.java.clientStackOverflow.StackOverflowClient;
import edu.java.clientStackOverflow.StackOverflowResponse;
import edu.java.domain.dto.LinkDto;
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
    public boolean update(LinkDto linkDto) {

        LinkDto link = linkDto;

        long questionId = Long.parseLong(link.url().getPath().split("/")[2]);

        StackOverflowResponse response = stackOverflowWebClient.fetchQuestion(questionId).blockFirst();

        OffsetDateTime lastCheck = OffsetDateTime.now();
        if (!link.lastUpdate().equals(response.lastActivityDate())) {
            link = new LinkDto(
                link.id(),
                link.url(),
                response.lastActivityDate(),
                lastCheck
            );
            linkService.update(link);
            return true;
        } else {
            link = new LinkDto(
                link.id(),
                link.url(),
                link.lastUpdate(),
                lastCheck
            );
            linkService.update(link);
            return false;
        }
    }

    @Override
    public String getHost() {
        return HOST;
    }
}
