package org.example.beerProj.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.beerProj.config.BasicIntegrationTests;
import org.example.beerProj.config.KafkaInitializer;
import org.example.beerProj.config.properties.KafkaProperties;
import org.example.beerProj.dto.EmployeeDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BasicKafkaTests extends BasicIntegrationTests {

    @Autowired
    protected KafkaProperties kafkaProperties;
    @Autowired
    protected KafkaTemplate<String, EmployeeDto> kafkaTemplate;

    protected List<ConsumerRecord<String, EmployeeDto>> results = new ArrayList<>();

    @BeforeEach
    void basicKafkaTestsSetUp() {
        KafkaInitializer.resetOffsets();
        results.clear();
    }

    @KafkaListener(
            topics = "#{@kafkaProperties.flows.employeeCreation.topic}",
            groupId = "tests-${random.uuid}"
    )
    public void listen(ConsumerRecord<String, EmployeeDto> record) {
        results.add(record);
    }


    protected Message<?> createMessage(KafkaProperties.FlowProperties properties, Object data) {
        return createMessage(properties.getTopic(), properties.getSource(), data);
    }

    protected Message<?> createMessage(String topic, String source, Object data) {
        return MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("source", source)
                .build();
    }
}