package org.example.sideservicefbeerproj.service;

import lombok.RequiredArgsConstructor;
import org.example.sideservicefbeerproj.dto.EmployeeByStore;
import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.example.sideservicefbeerproj.mapper.EmployeeMapper;
import org.example.sideservicefbeerproj.repository.EmployeeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Transactional
    @CacheEvict(value = "employeesByStore", key = "#employee.store")
    public EmployeeDto create(EmployeeDto employee) {
        return mapper.toDto(repository.save(mapper.toEntity(employee)));

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "employeeById", key = "#employeeId"),
            @CacheEvict(value = "employeesByStore", key = "#result")
    })
    public String deleteById(String employeeId) {
        EmployeeEntity entity = repository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.delete(entity);
        return entity.getStore();
    }

    @Cacheable(value = "employeesByStore", key = "#storeId")
    public EmployeeByStore getByStore(String storeId) {
        List<EmployeeEntity> entities = repository.findByStore(storeId);

        List<EmployeeDto> dtoList = entities.stream()
                .map(mapper::toDto)
                .toList();

        return EmployeeByStore.builder()
                .storeId(storeId)
                .employees(dtoList)
                .build();
    }
    @Transactional
    @CacheEvict(value = "employeesByStore", key = "#storeId")
    public void deleteByStore(String storeId) {
        repository.deleteAllByStore(storeId);
    }

    @Cacheable(value = "employeeById", key = "#employeeId")
    public EmployeeDto getById(String employeeId) {
        repository.findById(employeeId).orElseThrow(()  -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(repository.findById(employeeId).orElseThrow());
    }
}
