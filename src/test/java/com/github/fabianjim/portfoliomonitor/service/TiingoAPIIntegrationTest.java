package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.TiingoClient;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Instant;


@SpringBootTest
public class TiingoAPIIntegrationTest {

    @Autowired
    private TiingoClient tiingoClient;

    @Test
    void testConnectionToTiingo() {
        String response = tiingoClient.testConnection();
        assertNotNull(response);
        System.out.println(response);
        assertTrue(response.contains("You successfully sent a request"));
    }
    @Test
    public void testGetCurrentPrice() {
        double price = tiingoClient.getCurrentPrice("AAPL");
        System.out.println(price);
        assertNotNull(price);
    }
    @Test
    public void testGetStockData() {

        Stock mockStock = new Stock();
        mockStock.setCurrentPrice(200);
        mockStock.setTimestamp(Instant.parse("2025-08-01T14:30:00Z"));
        mockStock.setOpen(195);
        mockStock.setHigh(201);
        mockStock.setLow(199);
        mockStock.setPrevClose(190);

        TiingoClient tiingoClient = Mockito.mock(TiingoClient.class);
        when(tiingoClient.getStockData("AAPL")).thenReturn(mockStock);

        Stock apple = tiingoClient.getStockData("AAPL");

        assertEquals(200, apple.getCurrentPrice());
        assertEquals("2025-08-01T14:30:00Z", apple.getTimestamp());
        assertEquals(195, apple.getOpen());
        assertEquals(201, apple.getHigh());
        assertEquals(199, apple.getLow());
        assertEquals(190, apple.getPrevClose());
    }
}