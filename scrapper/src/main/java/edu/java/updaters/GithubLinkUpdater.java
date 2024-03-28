package edu.java.updaters;

import edu.java.clientGithub.GithubClient;
import edu.java.clientGithub.GithubResponse;
import edu.java.domain.dto.LinkDto;
import edu.java.services.LinkService;
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
    public String update(LinkDto linkdto) {

        LinkDto link = linkdto;

        List<String> argsFromUrl = List.of(link.url().getPath().split("/"));

        GithubResponse githubResponse =
            gitHubWebClient.fetchRepositoryInfo(
                argsFromUrl.get(1),
                argsFromUrl.get(2)
            ).blockFirst();

        OffsetDateTime lastCheck = OffsetDateTime.now();
        if (!link.lastUpdate().equals(githubResponse.createdAt())) {
            link = new LinkDto(
                link.id(),
                link.url(),
                githubResponse.createdAt(),
                lastCheck
            );
            linkService.update(link);
            return getResponseDescription(githubResponse);
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

    private String getResponseDescription(GithubResponse githubResponse) {
        return githubResponse.type() + " has occurred " +
            "in the repository " + githubResponse.repo().name() +
            " by " + githubResponse.actor().login();
    }
}
