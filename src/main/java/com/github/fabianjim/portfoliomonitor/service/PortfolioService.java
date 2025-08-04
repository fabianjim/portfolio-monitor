package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.api.TiingoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    public Portfolio currentPortfolio;

    private final MarketDataClient marketDataClient;

    @Autowired
    public PortfolioService(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    public void updatePortfolio(Portfolio portfolio) {
        this.currentPortfolio = portfolio;
    }

    public Portfolio getCurrentPortfolio() {
        return currentPortfolio;
    }

    public String testMarketDataClient() {
        return marketDataClient.testConnection();

    }

}
