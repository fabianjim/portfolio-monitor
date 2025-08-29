package com.github.fabianjim.portfoliomonitor.repository;

import com.github.fabianjim.portfoliomonitor.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    
    Optional<Stock> findByTicker(String ticker);
    
    Optional<Stock> findByTickerAndTimestamp(String ticker, String timestamp);
    
    boolean existsByTicker(String ticker);
    
    // Find the most recent stock data for a ticker
    Optional<Stock> findFirstByTickerOrderByTimestampDesc(String ticker);
    
    // Find all stock data for a ticker
    List<Stock> findByTickerOrderByTimestampDesc(String ticker);
}
