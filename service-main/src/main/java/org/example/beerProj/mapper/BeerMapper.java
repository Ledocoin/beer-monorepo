package org.example.beerProj.mapper;

import org.example.beerProj.dto.BeerRequest;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.entity.TypeEntity;
import org.example.beerProj.repository.TypeRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BeerMapper {

    @Autowired
    private TypeRepository typeRepository;

    @Mapping(target = "type", source = "type", qualifiedByName = "typeIdToName")
    public abstract BeerResponse toResponse(BeerEntity beerEntity); // GetMapping, метод для маппинга сущности в дто на отдачу пользователю

    @Mapping(target = "type", source = "type", qualifiedByName = "typeNameToId")
    public abstract BeerEntity toEntity(BeerRequest beerRequest); // PostMapping, метод для маппинга данных из дто от пользователя в сущность

    @Mapping(target = "type", source = "type", qualifiedByName = "typeNameToId")
    public abstract void updateEntityFromDto(BeerRequest beerRequest, @MappingTarget BeerEntity beerEntity); // PutMapping, метод для полного обновления данных в сущности, с затиранием неуказанных полей

    @Mapping(target = "type", source = "type", qualifiedByName = "typeNameToId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntityFromDto(BeerRequest dto, @MappingTarget BeerEntity entity);// PatchMapping, метод для частичного обновления данных в сущности, без затирания неуказанных полей

/// Вспомогательный метод для корректного преобразования данных связанных с таблицей Type
    @Named("typeNameToId")
    Long typeNameToId(String typeName) {
        if (typeName == null) {return null;}
        return typeRepository.findByName(typeName)
                .map(TypeEntity::getId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid type name: " + typeName));
    }

    @Named("typeIdToName")
    String typeIdToName(Long Id) {
        return typeRepository.findById(Id)
                .map(TypeEntity::getName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid type id: " + Id));
    }

}


