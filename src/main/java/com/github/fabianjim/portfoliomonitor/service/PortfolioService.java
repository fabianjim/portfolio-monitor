package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    public Portfolio currentPortfolio;

    private final MarketDataClient marketDataClient;

    public PortfolioService(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    public void updatePortfolio(Portfolio portfolio) {
        if (portfolio != null && portfolio.getHoldings() != null) {
            for (Holding holding : portfolio.getHoldings()) {
                if (holding != null && holding.getTicker() != null) {
                    Stock stock = marketDataClient.getStockData(holding.getTicker());
                    holding.setStock(stock);
                }
            }
        }
        this.currentPortfolio = portfolio;
    }

    public Portfolio getCurrentPortfolio() {
        return currentPortfolio;
    }

    public String testMarketDataClient() {
        return marketDataClient.testConnection();

    }

}
