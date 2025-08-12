package org.example.beerProj.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.beerProj.dto.StorageDto;
import org.example.beerProj.dto.StorageResponse;
import org.example.beerProj.service.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@Schema(description = "Контроллер для взаимодействия с таблицей склада")
public class StorageController {
    private final StorageService storageService;

    @GetMapping
    public Page<StorageResponse> getAllStorages(@RequestParam(required = false, defaultValue = "0") Integer pageNumber, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return storageService.getAllStorages(pageNumber,pageSize);
    }

    @PostMapping()
    public StorageResponse createStorage(@Valid @RequestBody StorageDto storageDto) {
        return storageService.createStorage(storageDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStorage(@PathVariable Long id) {
        storageService.deleteStorage(id);
    }

}
