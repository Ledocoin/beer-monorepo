package org.example.beerProj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class CreateStoreWithEmployeeDto extends StoreRequest{
    private List<EmployeeDto> employees;

    public CreateStoreWithEmployeeDto updateStoreId(String id) {
        employees = Optional.ofNullable(employees).orElse(new ArrayList<>())
                .stream()
                .map(EmployeeDto::toBuilder)
                .map(e -> e.store(id))
                .map(EmployeeDto.EmployeeDtoBuilder::build)
                .toList();
        return this;
    }
}
