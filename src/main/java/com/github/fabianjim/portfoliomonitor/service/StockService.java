package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        System.out.println("Fresh data: " + freshData.getTimestamp());
        Optional<Stock> existing = stockRepository.findByTickerAndTimestamp(
            ticker, freshData.getTimestamp());
        
        if (existing.isPresent()) {
            System.out.println(("Existing" + existing.get().getTimestamp()));
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

    public List<Stock> updateMultipleStocks(List<String> tickers) {
        List<Stock> updatedStocks = new ArrayList<>();
        for (String ticker : tickers) {
            try {
                System.out.println("Updating for ticker: " + ticker);
                Stock updatedStock = updateStockData(ticker);
                updatedStocks.add(updatedStock);
            } catch (Exception e) {
                System.err.println("Failed to update stock data for " + ticker + ": " + e.getMessage());
            }
        }
        return updatedStocks;
    }

    public boolean hasStockData(String ticker) {
        return stockRepository.existsByTicker(ticker);
    }
}
