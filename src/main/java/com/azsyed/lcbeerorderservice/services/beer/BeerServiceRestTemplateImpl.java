package com.azsyed.lcbeerorderservice.services.beer;

import com.azsyed.lcbeerorderservice.services.beer.domain.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@ConfigurationProperties(prefix = "com.azsyed",ignoreUnknownFields = false)
@Service
public class BeerServiceRestTemplateImpl implements BeerService {

    private RestTemplate restTemplate;
    private String beerServiceHost;

    public static final String V1_BEER_BY_ID_PATH = "/api/v1/beer/";
    public static final String V1_BEER_BY_UPC_PATH = "/api/v1/beer/upc/";

    public void setBeerServiceHost(String beerServiceHost) {
        this.beerServiceHost = beerServiceHost;
    }

    public BeerServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(beerServiceHost+V1_BEER_BY_UPC_PATH+upc.toString(),BeerDto.class));
    }
}
