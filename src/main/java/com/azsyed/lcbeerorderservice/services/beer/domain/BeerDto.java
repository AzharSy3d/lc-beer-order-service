package com.azsyed.lcbeerorderservice.services.beer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {

    private UUID id;
    private String beerName;
    private String beerStyle;
    private String upc;
    private BigDecimal price;

}
