package org.example.beerProj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerSearchCriteriaRequest {
    private String name;
    private String producer;
    private Long type;
    private BigDecimal price;
    private BigDecimal alcohol;
}
