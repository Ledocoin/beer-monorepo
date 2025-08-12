package org.example.sideservicefbeerproj.repository;

import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeRepository extends MongoRepository<EmployeeEntity, String> {
    List<EmployeeEntity> findByStore(String store);
    void deleteAllByStore(String store);
}
