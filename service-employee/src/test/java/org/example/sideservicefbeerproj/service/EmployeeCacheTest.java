package org.example.sideservicefbeerproj.service;


import org.assertj.core.api.Assertions;
import org.example.sideservicefbeerproj.config.MongoInitializer;
import org.example.sideservicefbeerproj.config.RedisInitializer;
import org.example.sideservicefbeerproj.dto.EmployeeByStore;
import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.example.sideservicefbeerproj.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigInteger;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = {MongoInitializer.class, RedisInitializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeCacheTest {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        RedisInitializer.flush();
    }
    private final String storeId = "12345678";

    private EmployeeEntity createChattini(String storeId){
        EmployeeEntity employee = EmployeeEntity.builder()
                .store(storeId)
                .name("Chatitni")
                .surname("Bombardini")
                .phone("380123456781")
                .email("cht@bmb.com")
                .position("backpack")
                .salary(BigInteger.valueOf(1000)).build();
        return employeeRepository.save(employee);
    }

    @Test
    void getByStore_ShouldCacheResult(){
        createChattini("87654321");
        var first = employeeService.getByStore("87654321");
        employeeRepository.deleteAll();
        var second = employeeService.getByStore("87654321");

        Cache cache = cacheManager.getCache("employeesByStore");
        Assertions.assertThat(cache).isNotNull();
        Assertions.assertThat(cache.get("87654321")).isNotNull();

        Assertions.assertThat(second.getEmployees()).hasSize(1);
    }

    @Test
    void create_ShouldEvictResultsFromCache(){
        createChattini("87654321");
        employeeService.getByStore("87654321");

        EmployeeDto dto = EmployeeDto.builder()
                .name("N")
                .surname("S")
                .phone("380126732781")
                .email("N@S.com")
                .position("WHO")
                .salary(BigInteger.valueOf(1000))
                .store("87654321")
                .build();

        employeeService.create(dto);

        Cache cache = cacheManager.getCache("employeesByStore");
        Assertions.assertThat(cache.get("87654321")).isNull();

        var updated = employeeService.getByStore("87654321");
        Assertions.assertThat(updated.getEmployees()).hasSize(2);
    }

    @Test
    void deleteById_ShouldEvictCacheForStore() {
        var saved = createChattini(storeId);
        employeeService.getByStore(storeId);

        employeeService.deleteById(saved.getId());

        Cache cache = cacheManager.getCache("employeeById");
        Assertions.assertThat(cache.get(storeId)).isNull();

        var updated = employeeService.getByStore(storeId);
        Assertions.assertThat(updated.getEmployees()).isEmpty();
    }

    @Test
    void deleteByStore_ShouldEvictCache() {
        createChattini(storeId);
        employeeService.getByStore(storeId);

        employeeService.deleteByStore(storeId);

        Cache cache = cacheManager.getCache("employeesByStore");
        Assertions.assertThat(cache.get(storeId)).isNull();

        var result = employeeService.getByStore(storeId);
        Assertions.assertThat(result.getEmployees()).isEmpty();
    }

    @Test
    void getByStore_ShouldCacheEmptyResult() {
        var first = employeeService.getByStore(storeId);
        createChattini(storeId);
        var second = employeeService.getByStore(storeId);

        Cache cache = cacheManager.getCache("employeesByStore");
        Assertions.assertThat(cache.get(storeId)).isNotNull();

        Assertions.assertThat(second.getEmployees()).isEmpty();
    }



    @Test
    void getByStore_ShouldUseCache() throws Exception {
        employeeRepository.save(createChattini(storeId));
        EmployeeByStore fromDb = employeeService.getByStore(storeId);
        Assertions.assertThat(fromDb.getEmployees().size()).isEqualTo(1);

        employeeRepository.deleteAll();
        EmployeeByStore fromCache = employeeService.getByStore(storeId);
        Assertions.assertThat(fromCache.getEmployees().size()).isEqualTo(1);

        employeeService.deleteByStore(storeId);
        EmployeeByStore afterEvict = employeeService.getByStore(storeId);
        Assertions.assertThat(afterEvict.getEmployees().size()).isEqualTo(0);
    }

    @Test
    void getById_ShouldCacheResult() {
        var saved = createChattini(storeId);
        var dto1 = employeeService.getById(saved.getId());

        Assertions.assertThat(dto1).isNotNull();

        employeeRepository.deleteAll();
        var dto2 = employeeService.getById(saved.getId());

        Cache cache = cacheManager.getCache("employeeById");
        Assertions.assertThat(cache).isNotNull();
        Assertions.assertThat(cache.get(saved.getId())).isNotNull();

        Assertions.assertThat(dto2).isEqualTo(dto1);
    }

    @Test
    void deleteById_ShouldEvictEmployeeByIdCache() {
        var saved = createChattini(storeId);
        employeeService.getById(saved.getId());

        employeeService.deleteById(saved.getId());

        Cache cache = cacheManager.getCache("employeeById");
        Assertions.assertThat(cache.get(saved.getId())).isNull();
    }
}
