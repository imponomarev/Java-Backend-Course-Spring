package edu.java.services;

import edu.java.api.client.BotClient;
import edu.java.api.model.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.kafka.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateSender {

    private final ApplicationConfig applicationConfig;
    private final BotClient botClient;
    private final ScrapperQueueProducer scrapperQueueProducer;

    public void sendUpdate(LinkUpdateRequest updateRequest) {
        if (applicationConfig.useQueue()) {
            scrapperQueueProducer.send(updateRequest);
        } else {
            botClient.retrySendUpdate(updateRequest);
        }
    }
}
