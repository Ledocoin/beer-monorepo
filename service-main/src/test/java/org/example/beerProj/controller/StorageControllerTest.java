package org.example.beerProj.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.beerProj.config.BasicIntegrationTests;
import org.example.beerProj.config.PostgresInitializer;
import org.example.beerProj.dto.StorageDto;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.entity.StoreEntity;
import org.example.beerProj.entity.StorageEntity;
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


public class StorageControllerTest  extends BasicIntegrationTests {
    private static final String BASE_URL = "/storage";

    private Long existingStorageId;

    @BeforeEach
    void setup() {
        storageRepository.deleteAll();
        beerRepository.deleteAll();
        storeRepository.deleteAll();

        BeerEntity beer = new BeerEntity();
        beer.setId("testBeer");
        beer.setName("Test Beer");
        beer.setProducer("Test Producer");
        beer.setPrice(BigDecimal.TEN);
        beer.setAlcohol(BigDecimal.ONE);
        beer.setType(1L);
        beerRepository.save(beer);

        StoreEntity store = new StoreEntity();
        store.setId("testStore");
        store.setAddress("123 Test St");
        store.setPhone("380646890876");
        storeRepository.save(store);

        StorageEntity storage = new StorageEntity();
        storage.setBeer("testBeer");
        storage.setStore("testStore");
        storage.setCount(5L);

        storage = storageRepository.save(storage);
        existingStorageId = storage.getId();
    }

    @Test
    void getAllStorages_shouldReturnPage() throws Exception {
        for(int i = 0; i < 9; i++){
            StorageEntity storage = new StorageEntity();
            storage.setBeer("testBeer");
            storage.setStore("testStore");
            storage.setCount(5L + i);
            storage = storageRepository.save(storage);
        }

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?pageNumber=2&pageSize=3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(2));
    }

    @Test
    void createStorage_shouldReturnCreatedStorage() throws Exception {
        StorageDto request = new StorageDto("testBeer", "testStore", 10L);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beer").value("testBeer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.store").value("testStore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(10));
    }

    @Test
    void createStorage_shouldReturnBadRequest_whenInvalid() throws Exception {
        // Missing beer and negative count
        String invalidJson = "{\"store\": \"testStore\", \"count\": -1}";

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createStorage_shouldReturnServerError_whenBeerOrStoreMissing() throws Exception {
        // Non-existent beer and store triggers DB constraint
        StorageDto request = new StorageDto("noBeer", "noStore", 5L);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }


    @Test
    void deleteStorage_shouldRemoveStorage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + existingStorageId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThat(storageRepository.findById(existingStorageId)).isEmpty();
    }

    @Test
    void deleteStorage_shouldReturnNotFound_whenMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/999999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
