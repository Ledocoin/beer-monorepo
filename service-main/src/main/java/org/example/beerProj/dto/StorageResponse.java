package org.example.beerProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@Schema(description = "Ответ на запрос в serviceStorage")
public class StorageResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String beer;

    @NotBlank
    private final String store;

    @NotNull
    private final Long count;
}
