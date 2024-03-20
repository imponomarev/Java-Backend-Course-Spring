package edu.java.updaters;

import edu.java.clientGithub.GithubClient;
import edu.java.clientGithub.GithubResponse;
import edu.java.domain.dto.LinkDto;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GithubLinkUpdater implements LinkUpdater {

    private final GithubClient gitHubWebClient;
    private final LinkService linkService;
    private static final String HOST = "github.com";

    @Override
    public boolean update(LinkDto linkdto) {

        LinkDto link = linkdto;

        List<String> argsFromUrl = List.of(link.url().getPath().split("/"));

        GithubResponse githubResponse =
            gitHubWebClient.fetchRepositoryInfo(argsFromUrl.get(1),
                argsFromUrl.get(2)).blockFirst();

        OffsetDateTime lastCheck = OffsetDateTime.now();
        if (!link.lastUpdate().equals(githubResponse.createdAt())) {
            link = new LinkDto(
                link.id(),
                link.url(),
                githubResponse.createdAt(),
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
    public boolean support(URI url) {
        return url.getHost().equals(HOST);
    }
}
