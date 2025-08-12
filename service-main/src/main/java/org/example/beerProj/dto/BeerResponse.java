package org.example.beerProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
@Schema(description = "Ответ на запрос к сервису пива")
public class BeerResponse {

    @NotBlank
    private final String id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String producer;
    @NotNull
    private final BigDecimal price;
    @NotNull
    private final BigDecimal alcohol;
    @NotBlank
    private final String type;
}
