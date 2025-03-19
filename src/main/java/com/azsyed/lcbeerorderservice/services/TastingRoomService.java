package com.azsyed.lcbeerorderservice.services;

import com.azsyed.brewery.model.BeerOrderDto;
import com.azsyed.brewery.model.BeerOrderLineDto;
import com.azsyed.lcbeerorderservice.bootstrap.BeerOrderBootStrap;
import com.azsyed.lcbeerorderservice.domain.Customer;
import com.azsyed.lcbeerorderservice.repositories.BeerOrderRepository;
import com.azsyed.lcbeerorderservice.repositories.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TastingRoomService {

    private final CustomerRepository customerRepository;
    private final BeerOrderService beerOrderService;
    private final BeerOrderRepository beerOrderRepository;
    private final List<String> beerUpcs = new ArrayList<>(3);

    public TastingRoomService(CustomerRepository customerRepository, BeerOrderService beerOrderService,
                              BeerOrderRepository beerOrderRepository) {
        this.customerRepository = customerRepository;
        this.beerOrderService = beerOrderService;
        this.beerOrderRepository = beerOrderRepository;

        beerUpcs.add(BeerOrderBootStrap.BEER_1_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_2_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_3_UPC);
    }

    /**
     * Places an order in the tasting room.
     * <p>
     * This method is responsible for initiating the ordering process in the tasting room. It will call the helper method {@link #doPlaceOrder(Customer)} to perform the actual ordering logic.
     *
     * @throws IllegalArgumentException if no customer is provided
     */
    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1) { //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    /**
     * Performs the actual order placement for a given customer.
     * <p>
     * This private method takes care of placing an order for the provided customer. It may involve querying databases, updating inventory, and communicating with external services.
     *
     * @param customer The customer for whom the order is being placed. Must not be null.
     * @throws IllegalArgumentException if the customer parameter is null
     */
    private void doPlaceOrder(Customer customer) {
        String beerToOrder = getRandomBeerUpc();

        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .upc(beerToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<BeerOrderLineDto> beerOrderLineSet = new ArrayList<>();
        beerOrderLineSet.add(beerOrderLine);

        BeerOrderDto beerOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .beerOrderLines(beerOrderLineSet)
                .build();

        BeerOrderDto savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrder);
        log.info("Order Placed");

    }

    /**
     * Retrieves a random beer UPC (Universal Product Code).
     * <p>
     * This method generates and returns a random UPC number that corresponds to a beer product. The UPC generated should be valid according to the standard format.
     *
     * @return A string representing a randomly generated beer UPC
     */
    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size() - 0));
    }
}
