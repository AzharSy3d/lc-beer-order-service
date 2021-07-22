package com.azsyed.lcbeerorderservice.sm.actions;

import com.azsyed.lcbeerorderservice.domain.BeerOrderEventEnum;
import com.azsyed.lcbeerorderservice.domain.BeerOrderStatusEnum;
import com.azsyed.lcbeerorderservice.services.BeerOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {


    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
        String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);

        log.error("Compensating Transaction... Validation Failed: "+beerOrderId);

    }
}
