package com.azsyed.lcbeerorderservice.services;

import com.azsyed.lcbeerorderservice.domain.BeerOrder;
import com.azsyed.lcbeerorderservice.domain.BeerOrderEventEnum;
import com.azsyed.lcbeerorderservice.domain.BeerOrderStatusEnum;
import com.azsyed.lcbeerorderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {
    public static final String ORDER_HEADER_ID = "ORDER_HEADER_ID" ;

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;

    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATE_ORDER);
        return savedBeerOrder;
    }

    @Override
    public void processValidationResult(UUID orderId, Boolean isValid) {
        BeerOrder beerOrder = beerOrderRepository.getById(orderId);

        if(isValid){
            sendBeerOrderEvent(beerOrder,BeerOrderEventEnum.VALIDATION_PASSED);
        } else{
            sendBeerOrderEvent(beerOrder,BeerOrderEventEnum.VALIDATION_FAILED);
        }
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
        Message<BeerOrderEventEnum> msg = MessageBuilder.withPayload(eventEnum).build();

        sm.sendEvent((Mono<Message<BeerOrderEventEnum>>) msg);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());
        sm.stopReactively();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> sma.resetStateMachineReactively(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null)));
        return sm;
    }


}
