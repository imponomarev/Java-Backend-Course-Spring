package edu.java.kafka;

import edu.java.api.model.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdateRequest linkUpdateRequest) {
        kafkaTemplate.send(applicationConfig.kafka().topicName(), linkUpdateRequest);
    }
}
