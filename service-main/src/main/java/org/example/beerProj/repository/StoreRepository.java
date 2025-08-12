package org.example.beerProj.repository;

import org.example.beerProj.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, String> {
    List<StoreEntity> findAll();
    Page<StoreEntity> findAll(Pageable pageable);
}
