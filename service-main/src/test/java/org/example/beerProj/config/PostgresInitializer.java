package org.example.beerProj.config;


import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String CONTAINER_VERSION = "postgres:14.17";
    private static final PostgreSQLContainer<?> CONTAINER = createContainer();

    private static PostgreSQLContainer<?> createContainer() {
        var container = new PostgreSQLContainer<>(
                DockerImageName.parse(CONTAINER_VERSION)
        );
        container.withDatabaseName("mydb")
                .withUsername("sa")
                .withPassword("sa");
        container.withReuse(true);
        return container;
    }

    private static void start() {
        CONTAINER.start();
    }

    @Override
    public void initialize( ConfigurableApplicationContext applicationContext) {
        start();

        TestPropertyValues.of(
                "spring.datasource.url: %s".formatted(CONTAINER.getJdbcUrl()),
                "spring.datasource.username: %s".formatted(CONTAINER.getUsername()),
                "spring.datasource.password: %s".formatted(CONTAINER.getPassword())
        ).applyTo(applicationContext);
    }
}
