package com.azsyed.lcbeerorderservice.web.mappers;

import com.azsyed.brewery.model.CustomerDto;
import com.azsyed.lcbeerorderservice.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}