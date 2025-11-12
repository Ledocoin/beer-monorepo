package org.example.beerProj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.beerProj.config.BasicIntegrationTests;
import org.example.beerProj.config.PostgresInitializer;
import org.example.beerProj.dto.BeerRequest;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.repository.BeerRepository;
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
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


public class BeerControllerTest  extends BasicIntegrationTests {

    private static final String BASE_URL = "/beer";
    private String existingBeerId;

    @BeforeEach
    void setUp() {
        beerRepository.deleteAll();
        BeerEntity beer = BeerEntity.builder()
                .id("testId")
                .name("Cold Ipa")
                .producer("Finland")
                .price(BigDecimal.valueOf(2.50))
                .alcohol(BigDecimal.valueOf(6.5))
                .type(4L)
                .build();
        beerRepository.save(beer);
        existingBeerId = beer.getId();
    }

    @Test
    void getBeerById_shouldReturnBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + existingBeerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingBeerId));
    }

    @Test
    void getBeerById_shouldReturnNotFound_whenMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllBeers_shouldReturnPage() throws Exception {
        beerRepository.deleteAll();


        List<BeerEntity> beers = IntStream.range(0,10)
                .mapToObj(i -> BeerEntity.builder()
                        .name("Beer" + i)
                        .producer("Producer" + i)
                        .price(BigDecimal.valueOf(10 + i))
                        .alcohol(BigDecimal.valueOf(4.5 + i))
                        .type(4L)
                        .build())
                .toList();
        beerRepository.saveAll(beers);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .queryParam("pageNumber","2")
                        .queryParam("pageSize","3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(2));

    }

    @Test
    void createBeer_shouldReturnCreatedBeer() throws Exception {
        BeerRequest request = new BeerRequest("                 New             Beer                    ", "        nEW        PRODUCER               ", BigDecimal.TEN, BigDecimal.ONE, "IPA");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Beer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.producer").value("New producer"));
    }

    @Test
    void createBeer_shouldReturnBadRequest_whenInvalid() throws Exception {
        // Missing name and negative price
        String invalidJson = "{\"producer\": \"Prod\", \"price\": -5, \"alcohol\": 5, \"type\": \"IPA\"}";

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void putBeer_shouldReturnUpdatedBeer() throws Exception {
        BeerRequest request = new BeerRequest("Updated Beer", "Updated Producer", BigDecimal.valueOf(9.99), BigDecimal.valueOf(5), "IPA");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + existingBeerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Beer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.producer").value("Updated producer"));
    }

    @Test
    void putBeer_shouldReturnNotFound_whenMissing() throws Exception {
        BeerRequest request = new BeerRequest("X", "Y", BigDecimal.ONE, BigDecimal.ONE, "ALE");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/missingId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void patchBeer_shouldReturnUpdatedBeer() throws Exception {
        String patchJson = "{\"name\": \"Patched Beer\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + "/" + existingBeerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Patched Beer"));
    }

    @Test
    void patchBeer_shouldReturnNotFound_whenMissing() throws Exception {
        String patchJson = "{\"name\": \"Patched\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + "/notExist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteBeer_shouldReturnDeletedBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + existingBeerId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThat(beerRepository.findById(existingBeerId)).isEmpty();
    }

    @Test
    void deleteBeer_shouldReturnNotFound_whenMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/noId"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }




}
