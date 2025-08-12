package org.example.beerProj.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beerProj.annotation.Measure;
import org.example.beerProj.dto.BeerRequest;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.mapper.BeerMapper;
import org.example.beerProj.repository.BeerRepository;
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
public class BeerService {

    private final BeerRepository beerRepository;

    private final BeerMapper beerMapper;

    public Page<BeerResponse> getAllBeers(Integer page, Integer size) {
        return beerRepository.findAll(PageRequest.of(page, size))
                .map(beerMapper::toResponse);
    }

    public BeerResponse getBeerById(@Valid String id) {
        return beerRepository.findById(id)
                .map(beerMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Measure
    @Transactional
    public BeerResponse createNewBeer(@Valid BeerRequest beerRequest) {
        return beerMapper.toResponse(beerRepository.save(beerMapper.toEntity(beerRequest)));
    }

    @Transactional
    public BeerResponse putBeer(String id, @Valid BeerRequest beerRequest) {
        BeerEntity entity = beerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        beerMapper.updateEntityFromDto(beerRequest, entity);
        return beerMapper.toResponse(beerRepository.save(entity));
    }

    @Transactional
    public BeerResponse patchBeer(String id, BeerRequest beerRequest) {
        BeerEntity entity = beerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        beerMapper.patchEntityFromDto(beerRequest, entity);
        return beerMapper.toResponse(beerRepository.save(entity));
    }

    @Transactional
    public void deleteBeer(String id) {
        BeerEntity entity = beerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        beerRepository.delete(entity);
    }
}
