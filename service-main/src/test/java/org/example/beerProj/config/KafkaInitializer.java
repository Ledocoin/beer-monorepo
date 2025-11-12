package org.example.beerProj.config;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String CONTAINER_VERSION = "confluentinc/cp-kafka:7.9.2";
    private static final ConfluentKafkaContainer CONTAINER = createContainer();

    private static ConfluentKafkaContainer createContainer() {
        var container = new ConfluentKafkaContainer(DockerImageName.parse(CONTAINER_VERSION));
        container.withReuse(true);
        return container;
    }

    private static void start() {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
    }

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        start();
        TestPropertyValues.of(
                "spring.kafka.bootstrap-servers: %s".formatted(CONTAINER.getBootstrapServers())
        ).applyTo(applicationContext);
    }

    @SneakyThrows
    public static void resetOffsets() {
        if (CONTAINER.isRunning()) {
            CONTAINER.execInContainer("kafka-consumer-groups",
                    "--bootstrap-server", CONTAINER.getBootstrapServers(),
                    "--all-groups",
                    "--reset-offsets",
                    "--to-earliest",
                    "--execute"
            );
        }
    }
}