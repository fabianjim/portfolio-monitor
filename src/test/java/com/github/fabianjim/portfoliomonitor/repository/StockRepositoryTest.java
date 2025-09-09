package com.github.fabianjim.portfoliomonitor.repository;

import com.github.fabianjim.portfoliomonitor.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

@DataJpaTest
@ActiveProfiles("test")
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void testSaveAndFindStock() {
        // Create a test stock
        Stock stock = new Stock("TEST", Instant.parse("2024-01-01T00:00:00Z"), 100.0, 99.0, 98.0, 101.0, 97.0);
        
        // Save the stock
        Stock savedStock = stockRepository.save(stock);
        
        // Verify it was saved with an ID
        assertNotNull(savedStock.getId());
        assertEquals("TEST", savedStock.getTicker());
        
        // Find by ticker
        Stock foundStock = stockRepository.findByTicker("TEST").orElse(null);
        assertNotNull(foundStock);
        assertEquals("TEST", foundStock.getTicker());
        assertEquals(100.0, foundStock.getCurrentPrice());
    }
}
