package org.example.beerProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.beerProj.client.dto.EmployeeDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoreEmployeeInfoDto {

    @Schema(example = "W_cPwW5eqk9kxe2OxgivJzVgu", description = "Id в формате NanoId")
    @NotBlank
    @Size(min = 25, max = 25)
    private String id;

    @Schema(example = "Ленинградское ш., 58с53, Москва", description = "Адрес магазина с большой буквы")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Адрес магазина не должен содержать больше 50 символов")
    private String address;

    @Schema(description = "Номер телефона сотрудника", example = "380666667245")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 12, max = 12, message = "Телефон сотрудника должен содержать 12 символов")
    private String phone;

    @Schema(description = "Список работников магазина")
    private List<EmployeeDto> employees;
}