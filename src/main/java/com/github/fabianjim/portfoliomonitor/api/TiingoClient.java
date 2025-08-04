package com.github.fabianjim.portfoliomonitor.api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TiingoClient implements MarketDataClient {

    @Value("${tiingo.api.token}")
    private String apiToken;

    private final String baseUrl = "https://api.tiingo.com";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public double getPrice(String ticker) {
        return 0;
    }

    @Override
    public String testConnection() {
        String url = baseUrl + "/api/test/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
        );

        return response.getBody();
    }


}

