package org.example.sideservicefbeerproj.kafka;


import org.example.sideservicefbeerproj.config.KafkaInitializer;
import org.example.sideservicefbeerproj.config.MongoInitializer;
import org.example.sideservicefbeerproj.config.RedisInitializer;
import org.example.sideservicefbeerproj.config.properties.KafkaProperties;
import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.example.sideservicefbeerproj.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigInteger;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"kafka-listener", "test"})
@ContextConfiguration(initializers = {MongoInitializer.class, RedisInitializer.class, KafkaInitializer.class})
class EmployeeKafkaListenerTest {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        KafkaInitializer.resetOffsets();
        employeeRepository.deleteAll();
        RedisInitializer.flush();
    }

    @Test
    void listen_withMatchingSource_persistsEmployee() {
        EmployeeDto payload = EmployeeDto.builder()
                .id("employee-1")
                .name("John")
                .surname("Doe")
                .phone("380509007892")
                .email("john.doe@example.com")
                .position("seller")
                .salary(BigInteger.valueOf(45_000))
                .store("STOR1234")
                .build();

        kafkaTemplate.send(createMessage(payload, kafkaProperties.getFlows().getEmployeeCreation().getSource()));

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(employeeRepository.findAll()).hasSize(1));

        EmployeeEntity saved = employeeRepository.findAll().get(0);
        assertThat(saved.getId()).isEqualTo(payload.getId());
        assertThat(saved.getName()).isEqualTo(payload.getName());
        assertThat(saved.getSurname()).isEqualTo(payload.getSurname());
        assertThat(saved.getStore()).isEqualTo(payload.getStore());
        assertThat(saved.getEmail()).isEqualTo(payload.getEmail());
        assertThat(saved.getPosition()).isEqualTo(payload.getPosition());
        assertThat(saved.getSalary()).isEqualTo(payload.getSalary());
    }

    @Test
    void listen_withUnexpectedSource_doesNotPersist() {
        EmployeeDto payload = EmployeeDto.builder()
                .id("employee-2")
                .name("Jane")
                .surname("Smith")
                .phone("380509007892")
                .email("jane.smith@example.com")
                .position("manager")
                .salary(BigInteger.valueOf(55_000))
                .store("STORE999")
                .build();

        kafkaTemplate.send(createMessage(payload, "external-service"));

        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .during(Duration.ofSeconds(1))
                .untilAsserted(() -> assertThat(employeeRepository.findAll()).isEmpty());
    }

    private Message<EmployeeDto> createMessage(EmployeeDto payload, String source) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, kafkaProperties.getFlows().getEmployeeCreation().getTopic())
                .setHeader("source", source)
                .build();
    }
}
