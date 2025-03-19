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
     * Lists a paginated list of customers based on the provided page number and page size.
     *
     * @param pageNumber The current page number (0-based). If not provided, defaults to 0.
     * @param pageSize   The number of customers per page. If not provided, defaults to 10.
     * @return A {@link CustomerPagedList} containing the list of customers and pagination details.
     * @throws IllegalArgumentException if the pageNumber or pageSize is negative.
     *
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
