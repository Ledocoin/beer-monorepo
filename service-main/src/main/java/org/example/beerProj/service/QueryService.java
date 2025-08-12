package org.example.beerProj.service;

import lombok.RequiredArgsConstructor;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.mapper.BeerMapper;
import org.example.beerProj.mapper.StoreMapper;
import org.example.beerProj.repository.BeerRepository;
import org.example.beerProj.repository.StorageRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {

    private final BeerRepository beerRepository;
    private final StorageRepository storageRepository;
    private final BeerMapper beerMapper;
    private final StoreMapper storeMapper;


    public Page<BeerResponse> findBeersByName(String namePart, Pageable pageable) {
        return beerRepository.findBeersByNamePart(namePart, pageable)
                .map(beerMapper::toResponse);
    }

    // 2. Поиск магазинов, где есть конкретное пиво
    public Page<StoreResponse> findStoresWithBeer(String beerId, Pageable pageable) {
        return storageRepository.findStoresWithBeer(beerId, pageable)
                .map(storeMapper::toResponse);
    }

    // 3. Поиск магазинов, где есть все пива из списка
    public Page<StoreResponse> findStoresWithAllBeers(List<String> beerIds, Pageable pageable) {
        if (beerIds == null || beerIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return storageRepository.findStoresWithAllBeers(beerIds, pageable)
                .map(storeMapper::toResponse);
    }

    // 4. Поиск пива в конкретном магазине
    public Page<BeerResponse> findBeersInStore(String storeId, Pageable pageable) {
        return storageRepository.findBeersInStore(storeId, pageable)
                .map(beerMapper::toResponse);
    }

    public Page<BeerResponse> findBeersByCriteria(
            String name,
            String producer,
            Long type,
            BigDecimal price,
            BigDecimal alcohol,
            Pageable pageable
    ) {
        return beerRepository.findBeersByCriteria(name, producer, type, price, alcohol, pageable)
                .map(beerMapper::toResponse);
    }

}
