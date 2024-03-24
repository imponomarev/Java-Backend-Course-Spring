package edu.java;

import edu.java.api.client.BotClient;
import edu.java.api.model.LinkUpdateRequest;
import edu.java.domain.dto.LinkDto;
import edu.java.services.LinkService;
import edu.java.updaters.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final LinkService linkService;
    private final BotClient botClient;
    private final List<? extends LinkUpdater> linkUpdatersList;

    @Value("#{@scheduler.secondsThreshold}")
    private long threshold;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        List<LinkDto> oldLinks = linkService.getOldLinks(threshold);

        log.info("links that haven't been updated for a long time" + oldLinks.stream()
            .map(LinkDto::id)
            .toList());

        for (var link : oldLinks) {
            LinkUpdater linkUpdater = linkUpdatersList.stream()
                .filter(updater -> updater.support(link.url()))
                .findFirst()
                .get();
            if (linkUpdater.update(link)) {
                List<Long> chatIds = linkService.getChatIdsOfLink(link.id());
                botClient.sendUpdate(
                    new LinkUpdateRequest(
                        link.id(),
                        link.url(),
                        link.url().toString() + "updated",
                        chatIds
                    )
                );
            }
        }
    }
}
