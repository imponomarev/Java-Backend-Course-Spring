package edu.java.bot.configuration;

import edu.java.api.model.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final ApplicationConfig applicationConfig;

    @Bean
    public ConsumerFactory<String, LinkUpdateRequest> consumerFactory() {

        Map<String, Object> props = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, applicationConfig.kafka().groupId()
        );

        JsonDeserializer<LinkUpdateRequest> deserializer = new JsonDeserializer<>(LinkUpdateRequest.class);
        deserializer.addTrustedPackages("edu.java.api.model", "edu.java.bot.api.model");

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> dlqKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
            Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
            ),
            new StringSerializer(),
            new JsonSerializer<>()
        ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> kafkaListenerContainerFactory(
        ConsumerFactory<String, LinkUpdateRequest> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
