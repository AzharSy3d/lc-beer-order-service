package com.azsyed.lcbeerorderservice.services;

import com.azsyed.lcbeerorderservice.domain.BeerOrder;

import java.util.UUID;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID orderId, Boolean valid);
}
