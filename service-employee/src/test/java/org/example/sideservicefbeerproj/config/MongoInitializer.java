package org.example.sideservicefbeerproj.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String MONGO_IMAGE = "mongo:7.0";
    private static final String DATABASE_NAME = "ssdb";

    private static final MongoDBContainer CONTAINER = createContainer();

    private static MongoDBContainer createContainer() {
        MongoDBContainer container = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));
        container.withReuse(true);
        return container;
    }

    private static void start() {
        CONTAINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        start();

        TestPropertyValues.of(
                "spring.data.mongodb.uri=" + CONTAINER.getReplicaSetUrl(),
                "spring.data.mongodb.database=" + DATABASE_NAME
        ).applyTo(applicationContext);
    }
}

