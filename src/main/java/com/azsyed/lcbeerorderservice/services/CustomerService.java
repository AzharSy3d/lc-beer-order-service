package com.azsyed.lcbeerorderservice.services;

import com.azsyed.brewery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);

}
