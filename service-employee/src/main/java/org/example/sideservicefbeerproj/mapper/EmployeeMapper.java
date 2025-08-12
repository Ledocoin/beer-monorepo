package org.example.sideservicefbeerproj.mapper;

import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    public EmployeeEntity toEntity(EmployeeDto dto);
    public EmployeeDto toDto(EmployeeEntity entity);

}
