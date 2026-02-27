package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.model.Stock.StockType;
import com.github.fabianjim.portfoliomonitor.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    public Optional<Stock> getStockData(String ticker, Instant timestamp) {
        return stockRepository.findByTickerAndTimestamp(ticker, timestamp);
    }

    public List<Stock> getHistoricalStockData(String ticker) {
        return stockRepository.findByTickerOrderByTimestampDesc(ticker);
    }

    public Stock updateStockData(String ticker, StockType type) {
        return updateStockData(ticker, type, null);
    }

    public Stock updateStockData(String ticker, StockType type, Portfolio portfolio) {
        Stock freshData = marketDataClient.getStockData(ticker, type);
        if(type == StockType.INITIAL) {
            freshData.setPortfolio(portfolio);
        }
        return stockRepository.save(freshData);
    }

    public List<Stock> updateMultipleStocks(List<String> tickers, StockType type) {
        return updateMultipleStocks(tickers, type, null);
    }

    public List<Stock> updateMultipleStocks(List<String> tickers, StockType type, Portfolio portfolio) {
        List<Stock> updatedStocks = new ArrayList<>();
        for (String ticker : tickers) {
            try {
                Stock updatedStock = updateStockData(ticker, type, portfolio);
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
