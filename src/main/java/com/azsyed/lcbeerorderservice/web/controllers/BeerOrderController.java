/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azsyed.lcbeerorderservice.web.controllers;

import com.azsyed.brewery.model.BeerOrderDto;
import com.azsyed.brewery.model.BeerOrderPagedList;
import com.azsyed.lcbeerorderservice.services.BeerOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    /**
     * Lists orders for a specific customer.
     *
     * @param customerId The unique identifier of the customer.
     * @param pageNumber The page number of results to return (optional).
     * @param pageSize The size of each page of results (optional).
     * @return A paged list of BeerOrderDto objects for the specified customer.
     */
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Places a new order for a customer.
     *
     * @param customerId The unique identifier of the customer.
     * @param beerOrderDto The details of the order to place.
     * @return The placed BeerOrderDto object.
     */
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    /**
     * Retrieves an order by its ID for a specific customer.
     *
     * @param customerId The unique identifier of the customer.
     * @param orderId The unique identifier of the order.
     * @return The specified BeerOrderDto object if found; throws NotFoundException otherwise.
     */
    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    /**
     * Marks an order as picked up by a customer.
     *
     * @param customerId The unique identifier of the customer.
     * @param orderId The unique identifier of the order to pick up.
     * @throws NotFoundException If the order is not found.
     * @throws IllegalStateException If the order cannot be picked up at this time (e.g., already picked up).
     */
    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
