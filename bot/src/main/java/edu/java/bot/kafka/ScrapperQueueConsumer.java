package edu.java.bot.kafka;

import edu.java.api.model.LinkUpdateRequest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperQueueConsumer {

    private final ApplicationConfig applicationConfig;
    private final BotService botService;
    private final KafkaTemplate<String, LinkUpdateRequest> dqlKafkaTemplate;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.group-id}")
    public void listen(LinkUpdateRequest linkUpdateRequest) {
        try {
            botService.add(linkUpdateRequest);
        } catch (Exception exception) {
            dqlKafkaTemplate.send(applicationConfig.kafka().badResponseTopicName(), linkUpdateRequest);
        }
    }
}
