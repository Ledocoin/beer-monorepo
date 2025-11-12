package org.example.beerProj.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.example.beerProj.dto.CreateStoreWithEmployeeDto;
import org.example.beerProj.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;


public class StoreKafkaListenerTests extends BasicKafkaTests {
    @Test
    void storeMessageShouldTriggerEmployeeCreation() {
        var employee = EmployeeDto.builder()
                .name("Иван")
                .surname("Иванов")
                .phone("79684551122")
                .email("ivan.ivanov@example.com")
                .position("продавец")
                .salary(BigInteger.valueOf(45_000))
                .store("Магазин у дома")
                .build();

        var storeWithEmployee = CreateStoreWithEmployeeDto.builder()
                .address("г. Тверь, ул. Советская, д. 12")
                .phone("79684551112")
                .employees(List.of(employee))
                .build();

        kafkaTemplate.send(createMessage(kafkaProperties.getFlows().getStoreCreation(), storeWithEmployee));

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> Assertions.assertThat(results).hasSize(1));

        ConsumerRecord<String, EmployeeDto> result = results.get(0);
        var source = Optional.ofNullable(result.headers().lastHeader("source"))
                .map(header -> new String(header.value(), StandardCharsets.UTF_8))
                .orElse(null);

        Assertions.assertThat(source)
                .isEqualTo(kafkaProperties.getFlows().getEmployeeCreation().getSource());
        Assertions.assertThat(result.value())
                .isEqualTo(employee);
    }
}
