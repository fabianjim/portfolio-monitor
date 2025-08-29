package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.api.MarketDataClient;
import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.repository.HoldingRepository;
import com.github.fabianjim.portfoliomonitor.repository.PortfolioRepository;
import com.github.fabianjim.portfoliomonitor.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PortfolioService {

    private final MarketDataClient marketDataClient;
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;

    public PortfolioService(MarketDataClient marketDataClient, 
                          PortfolioRepository portfolioRepository,
                          HoldingRepository holdingRepository,
                          StockRepository stockRepository) {
        this.marketDataClient = marketDataClient;
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
    }

    public void updatePortfolio(Portfolio portfolio) {
        if (portfolio != null && portfolio.getHoldings() != null) {
            for (Holding holding : portfolio.getHoldings()) {
                if (holding != null && holding.getTicker() != null) {
                    // Fetch fresh market data
                    Stock marketData = marketDataClient.getStockData(holding.getTicker());
                    
                    // Save or update the stock data
                    Stock existingStock = stockRepository.findByTicker(holding.getTicker()).orElse(null);
                    if (existingStock != null) {
                        // Update existing stock with new market data
                        existingStock.setCurrentPrice(marketData.getCurrentPrice());
                        existingStock.setOpen(marketData.getOpen());
                        existingStock.setPrevClose(marketData.getPrevClose());
                        existingStock.setHigh(marketData.getHigh());
                        existingStock.setLow(marketData.getLow());
                        existingStock.setTimestamp(marketData.getTimestamp());
                        holding.setStock(stockRepository.save(existingStock));
                    } else {
                        // Save new stock
                        holding.setStock(stockRepository.save(marketData));
                    }
                }
            }
            
            portfolioRepository.save(portfolio);
        }
    }

    public Portfolio getCurrentPortfolio() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        return portfolios.isEmpty() ? null : portfolios.get(0);
    }

    public String testMarketDataClient() {
        return marketDataClient.testConnection();

    }

}
