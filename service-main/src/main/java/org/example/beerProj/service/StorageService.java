package org.example.beerProj.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beerProj.dto.StorageDto;
import org.example.beerProj.dto.StorageResponse;
import org.example.beerProj.entity.StorageEntity;
import org.example.beerProj.mapper.StorageMapper;
import org.example.beerProj.repository.StorageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    public Page<StorageResponse> getAllStorages(Integer page, Integer size) {
        return storageRepository.findAll(PageRequest.of(page, size))
                .map(storageMapper::toResponse);
    }

    @Transactional
    public StorageResponse createStorage(@Valid StorageDto storageDto) {
        StorageEntity entity = storageMapper.toEntity(storageDto);
        storageRepository.save(entity);
        return storageMapper.toResponse(entity);
    }

    @Transactional
    public void deleteStorage(Long id) {
        StorageEntity entity = storageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        storageRepository.delete(entity);
    }
}
