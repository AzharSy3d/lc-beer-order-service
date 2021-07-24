package com.azsyed.lcbeerorderservice.bootstrap;

import com.azsyed.lcbeerorderservice.domain.Customer;
import com.azsyed.lcbeerorderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by jt on 2019-06-06.
 */
@RequiredArgsConstructor
@Component
public class BeerOrderBootStrap implements CommandLineRunner {
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "087162143151";
    public static final String BEER_2_UPC = "087162143152";
    public static final String BEER_3_UPC = "087162143153";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.findAllByCustomerNameLike(TASTING_ROOM).size() == 0) {
            Customer savedCustomer = customerRepository.saveAndFlush(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
            System.out.println("Saved Customer ID :"+savedCustomer.getId());

        }
    }
}
