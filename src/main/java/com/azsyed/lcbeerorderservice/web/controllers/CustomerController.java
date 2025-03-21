package com.azsyed.lcbeerorderservice.web.controllers;

import com.azsyed.brewery.model.CustomerPagedList;
import com.azsyed.lcbeerorderservice.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/")
@RestController
public class CustomerController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CustomerService customerService;

    /**
     * Lists customers based on pagination parameters.
     *
     * @param pageNumber The current page number to retrieve. If not provided, defaults to 1.
     * @param pageSize The number of customers per page. If not provided, defaults to 10.
     * @return A CustomerPagedList object containing the list of customers for the specified page.
     * @throws IllegalArgumentException if either pageNumber or pageSize is negative.
     */
    @GetMapping
    public CustomerPagedList listCustomers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return customerService.listCustomers(PageRequest.of(pageNumber, pageSize));
    }
}
