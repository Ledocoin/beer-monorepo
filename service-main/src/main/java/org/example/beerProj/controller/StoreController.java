package org.example.beerProj.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.example.beerProj.client.dto.EmployeeByStore;
import org.example.beerProj.client.dto.EmployeeDto;
import org.example.beerProj.dto.StoreEmployeeInfoDto;
import org.example.beerProj.dto.StoreRequest;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Schema(description = "Контроллер для взаимодействия с таблицей магазина")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public Page<StoreResponse> getAllStores(@RequestParam(required = false, defaultValue = "0") Integer pageNumber, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return storeService.getAllStores(pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public StoreResponse getStoreById(@Valid @PathVariable String id) {
        return storeService.getStoreById(id);
    }

    @PostMapping
    public StoreResponse createStore(@Valid @RequestBody StoreRequest storeRequest) {
        return storeService.createStore(storeRequest);
    }

    @PutMapping("/{id}")
    public StoreResponse updateStore(@PathVariable String id, @Valid @RequestBody StoreRequest storeRequest) {
        return storeService.putStore(id, storeRequest);
    }

    @PatchMapping("/{id}")
    public StoreResponse patchStore(@PathVariable String id, @RequestBody StoreRequest storeRequest) {
        return storeService.patchStore(id, storeRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteStore(@NotBlank
                            @PathVariable
                            @Parameter(description = "Id магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
                            String id) {
        storeService.deleteStore(id);
    }

    @GetMapping("/{id}/employees")
    public StoreEmployeeInfoDto getStoreEmployeeInfo(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id магазина", example = "Z_abcdef")
            String id) {
        return storeService.getStoreEmployeeInfo(id);
    }

    @PostMapping("/{id}/register-employees")
    public EmployeeByStore registerEmployees(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
            String id,
            @Valid
            @RequestBody
            @NotEmpty
            List<EmployeeDto> employees
    ) {
        return storeService.registerEmployees(id, employees);
    }
}
