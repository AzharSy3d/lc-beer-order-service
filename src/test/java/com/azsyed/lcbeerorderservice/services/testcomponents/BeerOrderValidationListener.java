package com.azsyed.lcbeerorderservice.services.testcomponents;

import com.azsyed.brewery.model.events.ValidateOrderRequest;
import com.azsyed.brewery.model.events.ValidateOrderResult;
import com.azsyed.lcbeerorderservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message msg){
        boolean isValid = true;
        boolean sendResponse = true;
        System.out.println("########### I RAN ########");

        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
        //condition to fail validation
        if (request.getBeerOrder().getCustomerRef() != null && request.getBeerOrder().getCustomerRef().equals("fail-validation")){
            isValid = false;
        }else if ("dont-validate".equals(request.getBeerOrder().getCustomerRef())){
            sendResponse = false;
        }
        if(sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateOrderResult.builder()
                            .isValid(isValid)
                            .orderId(request.getBeerOrder().getId())
                            .build());
        }

    }
}