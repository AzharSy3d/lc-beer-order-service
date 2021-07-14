package com.azsyed.lcbeerorderservice.sm.actions;

import com.azsyed.brewery.model.events.ValidateOrderRequest;
import com.azsyed.lcbeerorderservice.config.JmsConfig;
import com.azsyed.lcbeerorderservice.domain.BeerOrder;
import com.azsyed.lcbeerorderservice.domain.BeerOrderEventEnum;
import com.azsyed.lcbeerorderservice.domain.BeerOrderStatusEnum;
import com.azsyed.lcbeerorderservice.repositories.BeerOrderRepository;
import com.azsyed.lcbeerorderservice.services.BeerOrderManagerImpl;
import com.azsyed.lcbeerorderservice.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_HEADER_ID);
        BeerOrder beerOrder = beerOrderRepository.getById(UUID.fromString(beerOrderId));

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                .beerOrder(beerOrderMapper.beerOrderToDto(beerOrder))
                .build());

        log.debug("Sent Validation request to queue for order id :"+beerOrderId);
    }
}
