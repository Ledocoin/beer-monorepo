package org.example.beerProj.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.beerProj.dto.BeerRequest;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.service.BeerService;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("/beer")
@RequiredArgsConstructor
@Schema(description = "Контроллер для взаимодействия с таблицей пива")
public class BeerController {
    private final BeerService beerService;

    @GetMapping
    public Page<BeerResponse> getAllBeers(@RequestParam(required = false, defaultValue = "0") Integer pageNumber, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return beerService.getAllBeers(pageNumber,pageSize);
    }

    @GetMapping("/{id}")
    public BeerResponse getBeerById(@Valid @PathVariable String id) {
        return beerService.getBeerById(id);
    }

    @PostMapping
    public BeerResponse createBeer(@Valid @RequestBody BeerRequest beerRequest) {
        return beerService.createNewBeer(beerRequest);
    }

    @PutMapping("/{id}")
    public BeerResponse putBeer(@PathVariable String id, @Valid @RequestBody BeerRequest beerRequest) {
        return beerService.putBeer(id, beerRequest);
    }

    @PatchMapping("/{id}")
    public BeerResponse patchBeer(@PathVariable String id, @RequestBody BeerRequest beerRequest) {
        return beerService.patchBeer(id, beerRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteBeer(@PathVariable String id) {
        beerService.deleteBeer(id);
    }


}
