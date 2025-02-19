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
     * Places an order for a tasting room experience in the system.
     *
     * This method handles the process of placing an order for a tasting room experience. It checks the availability
     * of the requested tasting room and time slot, deducts the required number of tickets from the inventory,
     * and updates the database accordingly. If the order is successful, it returns without throwing any exceptions.
     * If there are no available tickets or another error occurs, an appropriate exception is thrown.
     *
     * @throws NoTicketsAvailableException if the requested tasting room and time slot are fully booked.
     * @throws InvalidTimeSlotException if the requested time slot is not valid for the tasting room.
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
     * Processes and places an order for a customer.
     *
     * @param customer The Customer object representing the details of the customer placing the order.
     * @throws InvalidOrderException If the order details are invalid or incomplete.
     * @throws InsufficientInventoryException If there is not enough inventory to fulfill the order.
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
     * Generates and returns a random beer UPC number as a String.
     *
     * @return A string representing a randomly generated UPC number for a beer.
     */

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size() - 0));
    }
}
