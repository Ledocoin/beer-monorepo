package org.example.beerProj.controller;

import org.example.beerProj.config.PostgresInitializer;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.entity.StorageEntity;
import org.example.beerProj.entity.StoreEntity;
import org.example.beerProj.repository.BeerRepository;
import org.example.beerProj.repository.StorageRepository;
import org.example.beerProj.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class QueryServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StorageRepository storageRepository;
    private BeerEntity build;

    @BeforeEach
    void setup() {
        storageRepository.deleteAll();
        beerRepository.deleteAll();
        storeRepository.deleteAll();

        BeerEntity beer = BeerEntity.builder()
                .id("beer1")
                .name("Test IPA")
                .producer("Test Brewery")
                .price(BigDecimal.valueOf(5.99))
                .alcohol(BigDecimal.valueOf(5.5))
                .type(1L)
                .build();

        StoreEntity store = StoreEntity.builder()
                .id("store1")
                .address("123 Test Street")
                .phone("3809990000009")
                .build();

        beerRepository.save(beer);
        storeRepository.save(store);

        StorageEntity storage = new StorageEntity();
        storage.setBeer("beer1");
        storage.setStore("store1");
        storage.setCount(50L);
        storageRepository.save(storage);
    }

    @Test
    void shouldReturnBeerByNamePart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/query/beers/by-name")
                        .param("namePart", "ipa")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Test IPA"));
    }

    @Test
    void shouldReturnStoresWithBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/query/stores/by-beer")
                        .param("beerId", "beer1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value("store1"));
    }


    //?
    @Test
    void shouldReturnStoresWithAllBeers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/query/stores/by-beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"beer1\"]"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }


    @Test
    void shouldReturnBeersInStore() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/query/beers/by-store")
                        .param("storeId", "store1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value("beer1"));
    }


    @Test
    void shouldReturnBeersByCriteria() throws Exception {
        String requestBody = """
        {
            "name": "IPA",
            "producer": "Test Brewery",
            "type": 1,
            "price": 5.99,
            "alcohol": 5.5
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/query/beers/by-criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Test IPA"));
    }

    @Test
    void shouldReturnEmptyForInvalidBeerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/query/stores/by-beer")
                        .param("beerId", "nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldReturnEmptyForEmptyBeerList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/query/stores/by-beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }


    @Test
    void shouldFailWithoutRequiredParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/query/beers/by-name")) // без namePart
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}
