package org.example.sideservicefbeerproj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sideservicefbeerproj.config.MongoInitializer;
import org.example.sideservicefbeerproj.config.RedisInitializer;
import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.example.sideservicefbeerproj.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigInteger;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = {MongoInitializer.class, RedisInitializer.class})
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private CacheManager cacheManager;


    private String BASE_URL = "/employee";

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
        clearCache("employeeById");
        clearCache("employeesByStore");
    }

    private void clearCache(String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) cache.clear();
    }

    private EmployeeDto createDto() {
        return EmployeeDto.builder()
                .name("TestName")
                .surname("TestSurname")
                .phone("380509007892")
                .email("test@test.com")
                .position("Bedolaga")
                .salary(BigInteger.valueOf(300))
                .store("kl19pg45")
                .build();
    }

    private String saveTestEmployeeAndGetId() {
        EmployeeEntity entity = mapper.convertValue(createDto(), EmployeeEntity.class);
        return employeeRepository.save(entity).getId();
    }

    @Test
    void createEmployee_ShouldReturnEmployee() throws Exception {
        EmployeeDto testDto = EmployeeDto.builder()
                .name("TestNameCreate")
                .surname("TestSurname")
                .phone("380509007892")
                .email("test@test.com")
                .position("Bedolaga")
                .salary(BigInteger.valueOf(300))
                .store("kl19jr27")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestNameCreate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("TestSurname"));

    }

    @Test
    void deleteEmployee() throws Exception {
        String id = saveTestEmployeeAndGetId();

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getByStore_ShouldReturnEmployeeByStore() throws Exception {
        EmployeeEntity e1 = mapper.convertValue(createDto(), EmployeeEntity.class);
        EmployeeEntity e2 = mapper.convertValue(createDto(), EmployeeEntity.class);
        employeeRepository.saveAll(List.of(e1, e2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/store/kl19pg45"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeId").value("kl19pg45"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees.length()").value(2));

    }

    @Test
    void deleteByStore_ShouldDelete() throws Exception {
        EmployeeEntity e1 = mapper.convertValue(createDto(), EmployeeEntity.class);
        EmployeeEntity e2 = mapper.convertValue(createDto(), EmployeeEntity.class);
        employeeRepository.saveAll(List.of(e1, e2));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/store/kl19pg45"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void delete_WhenIdIsInvalid_ThenThrowException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/invalid"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getByStore_WhenNoneExist_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/store/" + createDto().getStore()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeId").value(createDto().getStore()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").isEmpty());
    }

    @Test
    void deleteByStore_WhenNoneExist_ShouldSucceed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/store/" + createDto().getStore()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createEmployee_WithPredefinedId_ShouldSucceed() throws Exception {
        EmployeeDto dtoWithPredefinedId = createDto();
        dtoWithPredefinedId.setId("12345678");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoWithPredefinedId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("12345678"));
    }

    @Test
    void getByStore_ShouldReturnOnlyMatchingEmployees() throws Exception {
        EmployeeEntity e1 = mapper.convertValue(createDto(), EmployeeEntity.class);
        EmployeeEntity e2 = mapper.convertValue(createDto(), EmployeeEntity.class);
        e2.setName("AnotherName");
        e2.setEmail("another@test.com");
        employeeRepository.saveAll(List.of(e1, e2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/store/" + createDto().getStore()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].store").value(createDto().getStore()));
    }

}
