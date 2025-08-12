package org.example.beerProj.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beerProj.client.EmployeeClient;
import org.example.beerProj.client.dto.EmployeeByStore;
import org.example.beerProj.client.dto.EmployeeDto;
import org.example.beerProj.dto.StoreEmployeeInfoDto;
import org.example.beerProj.dto.StoreRequest;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.entity.StoreEntity;
import org.example.beerProj.mapper.StoreMapper;
import org.example.beerProj.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    private final EmployeeClient employeeClient;

    public StoreEmployeeInfoDto getStoreEmployeeInfo(String storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EmployeeByStore response = employeeClient.getEmployees(storeId);
        return StoreEmployeeInfoDto.builder()
                .id(storeEntity.getId())
                .address(storeEntity.getAddress())
                .phone(storeEntity.getPhone())
                .employees(response.getEmployees())
                .build();
    }


    public Page<StoreResponse> getAllStores(Integer page, Integer size) {
        return storeRepository.findAll(PageRequest.of(page, size))
                .map(storeMapper::toResponse);
    }

    public StoreResponse getStoreById(@Valid String id) {
        return storeRepository.findById(id)
                .map(storeMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public StoreResponse createStore(@Valid StoreRequest storeRequest) {
        return storeMapper.toResponse(storeRepository.save(storeMapper.toEntity(storeRequest)));
    }

    @Transactional
    public StoreResponse putStore(String id, @Valid StoreRequest storeRequest) {
        StoreEntity storeEntity = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        storeMapper.updateEntity(storeRequest, storeEntity);
        return storeMapper.toResponse(storeRepository.save(storeEntity));
    }

    @Transactional
    public StoreResponse patchStore(String id, StoreRequest storeRequest) {
        StoreEntity entity = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        storeMapper.patchEntityFromDto(storeRequest, entity);
        return storeMapper.toResponse(storeRepository.save(entity));
    }


    public void deleteStore(String id) {
        StoreEntity entity = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        employeeClient.deleteEmployees(id);

        storeRepository.delete(entity);
    }

    public EmployeeByStore registerEmployees(@NotBlank String id, @Valid List<EmployeeDto> employees) {
        storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EmployeeDto> createdEmployees = employees.stream()
                .map(e -> {
                    e.setStore(id);
                    return employeeClient.createEmployee(e);
                })
                .toList();

        return EmployeeByStore.builder()
                .storeId(id)
                .employees(createdEmployees)
                .build();
    }
}
