package org.example.beerProj.repository;

import org.example.beerProj.entity.BeerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BeerRepository extends CrudRepository<BeerEntity, String> {

    List<BeerEntity> findAll();
    Page<BeerEntity> findAll(Pageable pageable);

    @Query(
            value = "SELECT * FROM beer WHERE LOWER(name) LIKE LOWER(CONCAT('%', :namePart, '%'))",
            countQuery = "SELECT count(*) FROM beer WHERE LOWER(name) LIKE LOWER(CONCAT('%', :namePart, '%'))",
            nativeQuery = true
    )
    Page<BeerEntity> findBeersByNamePart(@Param("namePart") String namePart, Pageable pageable);


    ///⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣶⣶⣄⠀⠀⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠀⣀⣤⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣷⡄⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡄⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠻⠿⠿⠿⠯⠉⠻⢿⣿⡿⠋⠀⠀⠉⠈⠁⠀⢠⠀⠀⠀
    ///⠀⡀⠀⣶⠀⢠⣦⡄⣤⣠⣤⣶⣰⡂⣶⡆⣰⣶⣮⣴⣾⡇⠀⢸⠀⠀⠀
    ///⠀⡇⠀⢿⡀⢸⣿⣷⣽⣛⣟⣛⣿⣿⣿⣿⣾⣭⣭⣿⣿⣾⠀⢸⠀⠀⠀
    ///⠀⡇⠀⠘⠃⠘⣿⣿⣿⣿⣿⣿⣿⣛⣻⠟⠩⢿⣿⣿⣿⣿⠀⢸⡀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⢽⢻⣿⣿⠋⣩⠴⢷⣿⣿⠖⠲⣙⣿⣿⠏⠀⢸⠁⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠀⠛⢿⣿⠿⣿⣿⣿⡟⣿⣿⣿⡿⢿⡟⠀⠀⢸⠀⠀⠀
    ///⠀⠀⠀⠀⠀⣴⡄⠀⠈⠀⠀⠀⠿⢿⣿⡿⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠺⣿⣿⣄⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠙⠻⣷⣶⣤⣄⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠻⢿⣿⣿⣿⣿⣧⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀
    ///⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠛⠻⢿⣿⡇


    @Query(value = """
        SELECT * FROM beer
        WHERE (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:producer IS NULL OR LOWER(producer) LIKE LOWER(CONCAT('%', :producer, '%')))
          AND (:type IS NULL OR type = :type)
          AND (:price IS NULL OR price = :price)
          AND (:alcohol IS NULL OR alcohol = :alcohol)
        """,
            countQuery = """
        SELECT COUNT(*) FROM beer
        WHERE (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:producer IS NULL OR LOWER(producer) LIKE LOWER(CONCAT('%', :producer, '%')))
          AND (:type IS NULL OR type = :type)
          AND (:price IS NULL OR price = :price)
          AND (:alcohol IS NULL OR alcohol = :alcohol)
        """,
            nativeQuery = true)
    Page<BeerEntity> findBeersByCriteria(
            @Param("name") String name,
            @Param("producer") String producer,
            @Param("type") Long type,
            @Param("price") BigDecimal price,
            @Param("alcohol") BigDecimal alcohol,
            Pageable pageable
    );
}
