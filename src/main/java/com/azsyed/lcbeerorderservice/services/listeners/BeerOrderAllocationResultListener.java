package com.azsyed.lcbeerorderservice.services.listeners;

import com.azsyed.brewery.model.events.AllocateOrderResult;
import com.azsyed.lcbeerorderservice.config.JmsConfig;
import com.azsyed.lcbeerorderservice.services.BeerOrderManager;
import com.azsyed.lcbeerorderservice.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BeerOrderAllocationResultListener {

    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            beerOrderManager.beerOrderAllocationPassed(result.getBeerOrder());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrder());
        } else if(result.getAllocationError()){
            //allocation error
            beerOrderManager.beerOrderAllocationFailed(result.getBeerOrder());
        }
    }
}
