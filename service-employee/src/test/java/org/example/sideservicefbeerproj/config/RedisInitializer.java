package org.example.sideservicefbeerproj.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.utility.DockerImageName;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String CONTAINER_VERSION = "redis:7.2.5-alpine";
    private static final RedisContainer CONTAINER = createContainer();

    private static RedisContainer createContainer() {
        var container = new RedisContainer(
                DockerImageName.parse(CONTAINER_VERSION)
        );
        container.withReuse(true);
        return container;
    }

    private static void start() {
        CONTAINER.start();
    }

    public static void flush() {
        if (CONTAINER.isRunning()) {
            try{
                CONTAINER.execInContainer("redis-cli", "flushdb");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        start();
        TestPropertyValues.of(
                "spring.data.redis.url: %s".formatted(CONTAINER.getRedisURI())
        ).applyTo(applicationContext);
    }
}
