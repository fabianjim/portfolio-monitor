package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final MarketDataClient marketDataClient;
    private final StockRepository stockRepository;

    public StockService(MarketDataClient marketDataClient, StockRepository stockRepository) {
        this.marketDataClient = marketDataClient;
        this.stockRepository = stockRepository;
    }

    public Optional<Stock> getLatestStockData(String ticker) {
        return stockRepository.findFirstByTickerOrderByTimestampDesc(ticker);
    }

    public Optional<Stock> getStockData(String ticker, String timestamp) {
        return stockRepository.findByTickerAndTimestamp(ticker, timestamp);
    }

    public List<Stock> getHistoricalStockData(String ticker) {
        return stockRepository.findByTickerOrderByTimestampDesc(ticker);
    }

    public Stock updateStockData(String ticker) {
        Stock freshData = marketDataClient.getStockData(ticker);
        
        Optional<Stock> existing = stockRepository.findByTickerAndTimestamp(
            ticker, freshData.getTimestamp());
        
        if (existing.isPresent()) {
            Stock existingStock = existing.get();
            existingStock.setCurrentPrice(freshData.getCurrentPrice());
            existingStock.setOpen(freshData.getOpen());
            existingStock.setPrevClose(freshData.getPrevClose());
            existingStock.setHigh(freshData.getHigh());
            existingStock.setLow(freshData.getLow());
            return stockRepository.save(existingStock);
        } else {
            return stockRepository.save(freshData);
        }
    }

    public void updateMultipleStocks(List<String> tickers) {
        for (String ticker : tickers) {
            try {
                updateStockData(ticker);
            } catch (Exception e) {
                // Log error but continue with other tickers
                System.err.println("Failed to update stock data for " + ticker + ": " + e.getMessage());
            }
        }
    }

    public boolean hasStockData(String ticker) {
        return stockRepository.existsByTicker(ticker);
    }
}
