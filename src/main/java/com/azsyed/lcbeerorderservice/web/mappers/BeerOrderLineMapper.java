package com.azsyed.lcbeerorderservice.web.mappers;

import com.azsyed.lcbeerorderservice.domain.BeerOrderLine;
import com.azsyed.lcbeerorderservice.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
