package org.example.beerProj.mapper;

import org.example.beerProj.dto.StorageDto;
import org.example.beerProj.dto.StorageResponse;
import org.example.beerProj.entity.StorageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageMapper {

    StorageEntity toEntity(StorageDto storageDto);

    StorageResponse toResponse(StorageEntity storageEntity);
}
