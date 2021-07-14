package com.azsyed.brewery.model.events;

import com.azsyed.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllocateOrderResult {
    private BeerOrderDto beerOrder;
    private Boolean allocationError=false;
    private Boolean pendingInventory=false;
}
