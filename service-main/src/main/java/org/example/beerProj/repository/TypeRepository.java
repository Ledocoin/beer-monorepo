package org.example.beerProj.repository;

import org.example.beerProj.entity.TypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends CrudRepository<TypeEntity, Long> {

    Optional<TypeEntity> findByName(String name);
    Optional<TypeEntity> findById(Long id);
}
