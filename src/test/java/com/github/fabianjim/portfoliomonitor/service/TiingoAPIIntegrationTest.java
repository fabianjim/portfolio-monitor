package com.github.fabianjim.portfoliomonitor.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TiingoAPIIntegrationTest {

    @Autowired
    private PortfolioService portfolioService;

    @Test
    void testConnectionToTiingo() {
        String response = portfolioService.testMarketDataClient();
        assertNotNull(response);
        System.out.println(response);
        assertTrue(response.contains("You successfully sent a request"));
    }
}