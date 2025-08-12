package org.example.beerProj.repository;


import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.entity.StorageEntity;
import org.example.beerProj.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends CrudRepository<StorageEntity, Long> {
    Page<StorageEntity> findAll(Pageable pageable);

    @Query(value = """
        SELECT DISTINCT s.* FROM store s
        JOIN storage st ON s.id = st.store
        WHERE st.beer = :beerId
        """,
            countQuery = """
        SELECT COUNT(DISTINCT s.id) FROM store s
        JOIN storage st ON s.id = st.store
        WHERE st.beer = :beerId
        """,
            nativeQuery = true)
    Page<StoreEntity> findStoresWithBeer(@Param("beerId") String beerId, Pageable pageable);

    @Query(value = """
        SELECT s.* FROM store s
        WHERE NOT EXISTS (
            SELECT b.id FROM beer b
            WHERE b.id IN (:beerIds)
            EXCEPT
            SELECT st.beer FROM storage st WHERE st.store = s.id
        )
        """,
            countQuery = """
        SELECT COUNT(*) FROM store s
        WHERE NOT EXISTS (
            SELECT b.id FROM beer b
            WHERE b.id IN (:beerIds)
            EXCEPT
            SELECT st.beer FROM storage st WHERE st.store = s.id
        )
        """,
            nativeQuery = true)
    Page<StoreEntity> findStoresWithAllBeers(@Param("beerIds") List<String> beerIds, Pageable pageable);

    @Query(value = """
        SELECT b.* FROM beer b
        JOIN storage st ON b.id = st.beer
        WHERE st.store = :storeId
        """,
            countQuery = """
        SELECT COUNT(*) FROM beer b
        JOIN storage st ON b.id = st.beer
        WHERE st.store = :storeId
        """,
            nativeQuery = true)
    Page<BeerEntity> findBeersInStore(@Param("storeId") String storeId, Pageable pageable);




}
