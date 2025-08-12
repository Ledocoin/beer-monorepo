package org.example.beerProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.beerProj.annotation.Normalize;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
@Schema(description = "Пользовательский ввод информации о пиве")
public class BeerRequest {

    @NotBlank
    private final String name;

    @Normalize("#this == null ? null : #this.substring(0,1).toUpperCase() + #this.substring(1).toLowerCase()")
    @NotBlank
    private final String producer;

    @NotNull
    private final BigDecimal price;

    @NotNull
    private final BigDecimal alcohol;

    @NotBlank
    private final String type;

}
