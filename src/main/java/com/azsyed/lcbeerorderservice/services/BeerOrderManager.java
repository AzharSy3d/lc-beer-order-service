package com.azsyed.lcbeerorderservice.services;

import com.azsyed.lcbeerorderservice.domain.BeerOrder;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
