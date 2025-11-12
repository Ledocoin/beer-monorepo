package org.example.beerProj.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.example.beerProj.client.dto.EmployeeDto;
import org.example.beerProj.config.BasicIntegrationTests;
import org.example.beerProj.config.PostgresInitializer;
import org.example.beerProj.dto.StoreRequest;
import org.example.beerProj.entity.StoreEntity;
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
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;


public class StoreControllerTest extends BasicIntegrationTests {

    private static final String BASE_URL = "/store";
    private final String storeId = "12345678";

    @BeforeEach
    void setup() {
        storageRepository.deleteAll();
        storeRepository.deleteAll(); // на всякий случай
        StoreEntity store = new StoreEntity();
        store.setId("12345678");
        store.setAddress("ул. Тестовая 1");
        store.setPhone("380001112233");
        storeRepository.save(store);

    }

    @Test
    void getStoreById_shouldReturnStore() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + storeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(storeId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("ул. Тестовая 1"));
    }

    @Test
    void getStoreById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + "hahaha"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllStores_shouldReturnPage() throws Exception {
        storeRepository.deleteAll();

        List<StoreEntity> stores = IntStream.range(0, 10)
                .mapToObj(i -> StoreEntity.builder()
                        .address("address" + i)
                        .phone("34567890L")
                        .build())
                .toList();
        storeRepository.saveAll(stores);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?pageNumber=2&pageSize=3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(2));
    }


    @Test
    void createStore_shouldReturnCreatedStore() throws Exception {
        StoreRequest request = new StoreRequest("New Address", "9999999999L");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("New Address"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("9999999999L"));
    }

    @Test
    void updateStore_shouldReturnUpdatedStore() throws Exception {
        StoreRequest request = new StoreRequest("Updated Address", "1111111111L");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Updated Address"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("1111111111L"));
    }

    @Test
    void patchStore_shouldPartiallyUpdate() throws Exception {
        String patchJson = "{\"address\": \"Patched Address\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + "/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Patched Address"));
    }

    //--POST тесты
    @Test
    public void registerEmployees_shouldRegisterEmployees_success() throws Exception {
        var empl1 = EmployeeDto.builder()
                .id(null)
                .name("Иван")
                .surname("Петров")
                .phone("380667328183")
                .email("o.ex@gmail.com")
                .position("бедолага")
                .salary(BigInteger.valueOf(100))
                .store(storeId)
                .build();
        var empl2 = EmployeeDto.builder()
                .id(null)
                .name("Jane")
                .surname("Doe")
                .phone("380667329467")
                .email("g.s@gmail.com")
                .position("Бедолага2")
                .salary(BigInteger.valueOf(102))
                .store(storeId)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(List.of(empl1, empl2));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl1)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(empl1.toBuilder().id("id_1").build()))));//проверка содержания тут

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl2)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(empl2.toBuilder().id("id_2").build()))));

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/store/" + storeId + "/register-employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl1))));

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl2))));

        WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));

        Assertions.assertThat(result.getResponse().getContentAsString()).contains("employees");

    }


    @Test
    public void registerEmployees_shouldThrowError_when() throws Exception {
        var empl = EmployeeDto.builder()
                .id(null)
                .name("Иван")
                .surname("Петров")
                .phone("380667328183")
                .email("o.ex@gmail.com")
                .position("бедолага")
                .salary(BigInteger.valueOf(100))
                .store(storeId)
                .build();

        var empl2 = EmployeeDto.builder()
                .id(null)
                .name("Jane")
                .surname("Doe")
                .phone("380667329467")
                .email("g.s@gmail.com")
                .position("Бедолага2")
                .salary(BigInteger.valueOf(102))
                .store(storeId)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(List.of(empl, empl2));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl), true, true))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(empl.toBuilder().id("id_1").build()))));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl2), true, true))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                 "status": "404",
                                 "error": "Not Found",
                                 "message": "",
                                }
                                """)));//смотри сюда

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/" + storeId + "/register-employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl), true, true)));

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl2), true, true)));

        WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));

    }

    @Test
    public void registerEmployees_shouldReturnNotFound_whenStoreIdIsNotValid() throws Exception {
        var empl1 = EmployeeDto.builder()
                .id(null)
                .name("Иван")
                .surname("Петров")
                .phone("380667328183")
                .email("o.ex@gmail.com")
                .position("бедолага")
                .salary(BigInteger.valueOf(100))
                .store(storeId)
                .build();

        var empl2 = EmployeeDto.builder()
                .id(null)
                .name("Jane")
                .surname("Doe")
                .phone("380667329467")
                .email("g.s@gmail.com")
                .position("Бедолага2")
                .salary(BigInteger.valueOf(102))
                .store(storeId)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(List.of(empl1, empl2));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl1)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(empl1.toBuilder().id("id_1").build()))));//проверка содержания тут

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(empl2)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(empl2.toBuilder().id("id_2").build()))));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/" + "invalidId" + "/register-employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));
    }

    ///?а почему?
    @Test
    public void registerEmployees_shouldThrowException_whenEmployeeListIsEmpty() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(List.of());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/{storeId}/register-employees", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));
    }

    @Test
    public void registerEmployees_shouldThrowException_whenDtoIsInvalid() throws Exception {
        var shitpost = EmployeeDto.builder()
                .id(null)
                .name("kk")
                .surname("kk")
                .phone("kk")
                .email("kk")
                .position("kk")
                .salary(BigInteger.valueOf(5))
                .store(storeId)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(List.of(shitpost));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/" + storeId + "/register-employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
        WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));
    }

    //--GET тесты
    @Test
    public void getStoreEmployeeInfo_shouldReturn200() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "id": "12345678",
                          "employees": [
                            {
                              "id": "id_1",
                              "name": "Иван",
                              "surname": "Петров",
                              "phone": "380667328183",
                              "email": "o.ex@gmail.com",
                              "position": "бедолага",
                              "salary": 100,
                              "store": "12345678"
                            },
                            {
                              "id": "id_2",
                              "name": "Jane",
                              "surname": "Doe",
                              "phone": "380667329467",
                              "email": "g.s@gmail.com",
                              "position": "Бедолага2",
                              "salary": 102,
                              "store": "12345678"
                            }
                          ]
                        }
                        """)));

        mockMvc.perform(MockMvcRequestBuilders.get("/store/" + storeId + "/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].name").value("Иван"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[1].name").value("Doe"));

        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/" + storeId)));
    }

    @Test
    public void getStoreEmployeeInfo_shouldReturnNotFound_whenStoreDoNotExist() throws Exception {
        String invalidStoreId = "87654321";
        mockMvc.perform(MockMvcRequestBuilders.get("/store/" + invalidStoreId + "/employees"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        WireMock.verify(0, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/" + invalidStoreId)));
    }


    //--DELETE говнотесты, доделать
    @Test
    public void deleteStore_ShouldReturn200_whenFeignReturns200() throws Exception {
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/store/" + storeId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        WireMock.verify(1, WireMock.deleteRequestedFor(WireMock.urlPathEqualTo("/employee/store/" + storeId)));
    }

    @Test
    public void deleteStore_ShouldReturn404_whenFeignReturns404() throws Exception {
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/store/" + "storeId"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        WireMock.verify(0, WireMock.deleteRequestedFor(WireMock.urlPathEqualTo("/employee/store/" + "storeId")));
    }

    @Test
    public void registerEmployees_shouldThrowException_whenEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/store/{storeId}/register-employees", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        WireMock.verify(0,WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee/store/" + storeId)));
    }

    @Test
    public void registerEmployees_shouldSucceed_whenValid() throws Exception {
        var employee = EmployeeDto.builder()
                .id(null)
                .name("Иван")
                .surname("Петров")
                .phone("380667328183")
                .email("ivan.petrov@example.com")
                .position("Продавец")
                .salary(BigInteger.valueOf(1000))
                .store(storeId)
                .build();

        String json = objectMapper.writeValueAsString(List.of(employee));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(employee.toBuilder().id("id_1").build()))));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/{storeId}/register-employees", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }




    //--ОШИБКИ ВАЛИДАЦИИ
    @Test
    public void registerEmployees_shouldFail_whenNameIsBlank() throws Exception {
        var employee = validEmployee().toBuilder().name("").build();
        sendInvalidDtoAndExpectBadRequest(employee);
    }
    @Test
    public void registerEmployees_shouldFail_whenPhoneTooShort() throws Exception {
        var employee = validEmployee().toBuilder().phone("123").build();
        sendInvalidDtoAndExpectBadRequest(employee);
    }
    @Test
    public void registerEmployees_shouldFail_whenEmailInvalid() throws Exception {
        var employee = validEmployee().toBuilder().email("invalid-email").build();
        sendInvalidDtoAndExpectBadRequest(employee);
    }
    @Test
    public void registerEmployees_shouldFail_whenSalaryNegative() throws Exception {
        var employee = validEmployee().toBuilder().salary(BigInteger.valueOf(-100)).build();
        sendInvalidDtoAndExpectBadRequest(employee);
    }
    @Test
    public void registerEmployees_shouldFail_whenStoreIdInvalid() throws Exception {
        var employee = validEmployee().toBuilder().store("abc").build();
        sendInvalidDtoAndExpectBadRequest(employee);
    }




    private EmployeeDto validEmployee() {
        return EmployeeDto.builder()
                .id(null)
                .name("Иван")
                .surname("Петров")
                .phone("380667328183")
                .email("ivan.petrov@example.com")
                .position("Продавец")
                .salary(BigInteger.valueOf(1000))
                .store(storeId)
                .build();
    }

    private void sendInvalidDtoAndExpectBadRequest(EmployeeDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(List.of(dto));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/store/{storeId}/register-employees", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));
    }

}


