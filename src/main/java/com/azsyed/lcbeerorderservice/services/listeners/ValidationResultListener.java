package com.azsyed.lcbeerorderservice.services.listeners;

import com.azsyed.brewery.model.events.ValidateOrderResult;
import com.azsyed.lcbeerorderservice.config.JmsConfig;
import com.azsyed.lcbeerorderservice.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult orderResult){
        log.debug("AFTER RANNNN : In listener");
        UUID orderId = orderResult.getOrderId();

       beerOrderManager.processValidationResult(orderId,orderResult.getIsValid());
    }
}
