package com.azsyed.lcbeerorderservice.services.beer;

import com.azsyed.lcbeerorderservice.services.beer.domain.BeerDto;

import java.util.Optional;

public interface BeerService {

    Optional<BeerDto> getBeerByUpc(String upc);
}
