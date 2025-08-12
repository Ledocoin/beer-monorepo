package org.example.beerProj.controller;

import lombok.RequiredArgsConstructor;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.dto.BeerSearchCriteriaRequest;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.service.QueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
public class QueryController {
    private final QueryService queryService;

    // 1. Поиск пива по части названия
    @GetMapping("/beers/by-name")
    public Page<BeerResponse> findBeersByName(
            @RequestParam String namePart,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.findBeersByName(namePart, pageable);
    }

    // 2. Магазины, где продается конкретное пиво
    @GetMapping("/stores/by-beer")
    public Page<StoreResponse> findStoresWithBeer(
            @RequestParam String beerId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.findStoresWithBeer(beerId, pageable);
    }

    // 3. Магазины, где есть все пива из списка
    @PostMapping("/stores/by-beers")
    public Page<StoreResponse> findStoresWithAllBeers(
            @RequestBody List<String> beerIds,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.findStoresWithAllBeers(beerIds, pageable);
    }

    // 4. Пиво, доступное в магазине
    @GetMapping("/beers/by-store")
    public Page<BeerResponse> findBeersInStore(
            @RequestParam String storeId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.findBeersInStore(storeId, pageable);
    }

    // 5. Поиск по всем критериям
    @PostMapping("/beers/by-criteria")
    public Page<BeerResponse> findBeersByCriteria(
            @RequestBody BeerSearchCriteriaRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.findBeersByCriteria(
                request.getName(),
                request.getProducer(),
                request.getType(),
                request.getPrice(),
                request.getAlcohol(),
                pageable
        );
    }

}
