package org.example.beerProj.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.beerProj.config.properties.KafkaProperties;
import org.example.beerProj.dto.CreateStoreWithEmployeeDto;
import org.example.beerProj.service.AsyncStoreService;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("kafka-listener")
public class StoreKafkaListener {

    private final AsyncStoreService service;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    @KafkaListener(
            topics = "#{@kafkaProperties.flows.storeCreation.topic}",
            groupId = "#{@kafkaProperties.groupId}"
    )
    public void listen(ConsumerRecord<String, JsonNode> record) {
        var expectedSource = kafkaProperties.getFlows().getStoreCreation().getSource();
        var sourceHeader = Optional.ofNullable(record.headers().lastHeader("source"))
                .map(h -> new String(h.value(), StandardCharsets.UTF_8))
                .orElse(null);

        log.info("Received message  headers: {}, body: {}", record.headers(), record.value());

        if (!expectedSource.equals(sourceHeader)) {
            log.info("Expected source header: {}, but was: {}. Skipping", expectedSource, sourceHeader);
            return;
        }

        var payload = objectMapper.convertValue(record.value(), CreateStoreWithEmployeeDto.class);
        service.create(payload);
    }
}