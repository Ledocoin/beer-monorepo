package org.example.beerProj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.beerProj.repository.BeerRepository;
import org.example.beerProj.repository.StorageRepository;
import org.example.beerProj.repository.StoreRepository;
import org.example.beerProj.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(
        initializers = {
                KafkaInitializer.class,
                PostgresInitializer.class
        }
)
@ActiveProfiles({"kafka-listener", "test"}) // kafka-listener, functional-stream, integration-flow
public abstract class BasicIntegrationTests {
    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(10102))
            .configureStaticDsl(true)
            .build();
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected BeerRepository beerRepository;
    @Autowired
    protected StorageRepository storageRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected TypeRepository typeRepository;
    @Autowired
    protected MockMvc mockMvc;


    @BeforeEach
    void basicIntegrationTestsSetup() {
        storageRepository.deleteAll();
        storeRepository.deleteAll();
        beerRepository.deleteAll();
        WireMock.reset();
    }
}