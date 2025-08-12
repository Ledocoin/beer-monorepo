package org.example.sideservicefbeerproj.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {

    @Id
    private String id;

    @Schema(description = "Имя работника", example = "Иван")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Имя сотрудника не должно содержать больше 100 символов")
    private String name;

    @Schema(description = "Фамилия работника", example = "Иванов")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Фамилия сотрудника не должна содержать больше 100 символов")
    private String surname;

    @Schema(description = "Номер телефона сотрудника", example = "380666667245")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 12, max = 12, message = "Телефон сотрудника должен содержать 12 символов")
    private String phone;

    @Schema(description = "Почта сотрудника", example = "i.ivanov@gmail.com")
    @Email
    @Size(max = 100, message = "Эл. почта сотрудника не должна содержать больше 100 символов")
    private String email;

    @Schema(description = "Должность работника", example = "Уборщик")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Должность сотрудника не должна содержать больше 100 символов")
    private String position;

    @Schema(description = "Зарплата работника", example = "40000")
    @NotNull
    @Positive(message = "Зарплата должна быть положительной")
    @Digits(integer = 1000, fraction = 0, message = "Зарплата должна быть целой")
    private BigInteger salary;

    @Schema(description = "Id магазина в формате NanoId", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, max = 8, message = "Id магазина должен содержать 25 символов")
    private String store;

}
