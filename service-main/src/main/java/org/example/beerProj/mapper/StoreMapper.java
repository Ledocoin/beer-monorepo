package org.example.beerProj.mapper;

import org.example.beerProj.dto.StoreRequest;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.entity.StoreEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreMapper {
    StoreEntity toEntity(StoreRequest storeRequest);

    StoreResponse toResponse(StoreEntity storeEntity);

    void updateEntity(StoreRequest storeRequest, @MappingTarget StoreEntity storeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntityFromDto(StoreRequest dto, @MappingTarget StoreEntity Entity);
}
