package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.dto.PortfolioHistoryDTO;
import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.model.TrackedStock;
import com.github.fabianjim.portfoliomonitor.model.User;
import com.github.fabianjim.portfoliomonitor.repository.PortfolioRepository;
import com.github.fabianjim.portfoliomonitor.repository.StockRepository;
import com.github.fabianjim.portfoliomonitor.repository.TrackedStockRepository;
import com.github.fabianjim.portfoliomonitor.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final TrackedStockRepository trackedStockRepository;
    private final StockRepository stockRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                          StockService stockService,
                          UserRepository userRepository,
                          TrackedStockRepository trackedStockRepository,
                          StockRepository stockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;
        this.trackedStockRepository = trackedStockRepository;
        this.stockRepository = stockRepository;
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("No authenticated user found");
        }
        User user = (User) auth.getPrincipal();
        return user.getId();
    }

    public void createPortfolio(Portfolio portfolio) {
        Integer userId = getCurrentUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        portfolio.setUser(user);

        if (portfolio != null && portfolio.getHoldings() != null) {
            portfolioRepository.save(portfolio);

            // Track all tickers in the new portfolio
            for (Holding holding : portfolio.getHoldings()) {
                startTrackingStock(holding.getTicker());
                // Fetch initial price data immediately
                stockService.updateStockData(holding.getTicker(), Stock.StockType.INITIAL);
            }
        }
    }

    /**
     * Start tracking a stock ticker. If already tracked, increment holder count.
     */
    private void startTrackingStock(String ticker) {
        TrackedStock trackedStock = trackedStockRepository.findByTicker(ticker)
            .orElse(null);

        if (trackedStock == null) {
            trackedStock = new TrackedStock(ticker);
            trackedStockRepository.save(trackedStock);
        } else {
            trackedStock.incrementHolderCount();
            trackedStockRepository.save(trackedStock);
        }
    }

    /**
     * Stop tracking a stock ticker. Decrement holder count, delete if no holders remain.
     */
    private void stopTrackingStock(String ticker) {
        TrackedStock trackedStock = trackedStockRepository.findByTicker(ticker)
            .orElse(null);

        if (trackedStock != null) {
            trackedStock.decrementHolderCount();
            if (trackedStock.getHolderCount() <= 0) {
                trackedStockRepository.delete(trackedStock);
            } else {
                trackedStockRepository.save(trackedStock);
            }
        }
    }

    /**
     * Add a holding to the current user's portfolio.
     */
    public void addHolding(String ticker, double shares) {
        Portfolio portfolio = getPortfolio();
        if (portfolio == null) {
            throw new RuntimeException("No portfolio found for current user");
        }

        Holding newHolding = new Holding(ticker, shares);
        portfolio.getHoldings().add(newHolding);
        portfolioRepository.save(portfolio);

        // Start tracking and fetch initial data
        startTrackingStock(ticker);
        stockService.updateStockData(ticker, Stock.StockType.INITIAL);
    }

    /**
     * Remove a holding from the current user's portfolio.
     */
    public void removeHolding(String ticker) {
        Portfolio portfolio = getPortfolio();
        if (portfolio == null) {
            throw new RuntimeException("No portfolio found for current user");
        }

        portfolio.getHoldings().removeIf(h -> h.getTicker().equals(ticker));
        portfolioRepository.save(portfolio);

        // Stop tracking this stock
        stopTrackingStock(ticker);
    }

    public List<String> getTickersfromPortfolio(Portfolio portfolio) {
        List<String> tickers = portfolio.getHoldings().stream()
                .map(Holding::getTicker)
                .distinct()
                .toList();
        return tickers;
    }



    public boolean existsByUserId() {
        Integer userId = getCurrentUserId();
        return portfolioRepository.existsByUserId(userId);
    }


    public void updatePortfolio(Portfolio portfolio) {
        
        /* System.out.println(getCurrentUserId());
        Integer userId = getCurrentUserId();
        portfolio.setId(userId);
        if (portfolio != null && portfolio.getHoldings() != null) {
            List<String> tickers = portfolio.getHoldings().stream()
                .map(Holding::getTicker)
                .distinct()
                .toList();
            System.out.println(portfolio.getHoldings().stream()
                .map(Holding::getTicker)
                .distinct()
                .toList());
            
            stockService.updateMultipleStocks(tickers);
            
            portfolioRepository.save(portfolio);
        }
        */
    } 

    public Portfolio getPortfolio() {
        Integer userId = getCurrentUserId();
        return portfolioRepository.findByUserId(userId).orElse(null);
    }
    
    
    public Stock getStockData(String ticker) {
        return stockService.getLatestStockData(ticker).orElse(null);
    }

    public Stock getStockData(String ticker, Instant timestamp) {
        return stockService.getStockData(ticker, timestamp).orElse(null);
    }

    /**
     * Get top N trending stocks by holder count
     */
    public List<TrackedStock> getTopTrendingStocks(int limit) {
        return trackedStockRepository.findTopTrackedStocks(limit);
    }

    /**
     * Calculate portfolio value history for the current user's portfolio.
     * Returns list of portfolio values at each timestamp where all holdings have data.
     */
    public List<PortfolioHistoryDTO> getPortfolioHistory() {
        Portfolio portfolio = getPortfolio();
        if (portfolio == null || portfolio.getHoldings() == null || portfolio.getHoldings().isEmpty()) {
            return new ArrayList<>();
        }

        List<Holding> holdings = portfolio.getHoldings();
        
        // Map to store all stock data grouped by timestamp
        Map<Instant, Map<String, Stock>> dataByTimestamp = new HashMap<>();
        
        // Track which tickers we need data for and their holdings
        Map<String, Holding> holdingsByTicker = new HashMap<>();
        for (Holding holding : holdings) {
            holdingsByTicker.put(holding.getTicker(), holding);
        }

        // Fetch historical data for each holding
        for (Holding holding : holdings) {
            List<Stock> stockHistory = stockRepository.findByTickerOrderByTimestampDesc(holding.getTicker());
            
            for (Stock stock : stockHistory) {
                // Only include data from buy time onward
                if (!stock.getTimestamp().isBefore(holding.getBuyTimestamp())) {
                    dataByTimestamp
                        .computeIfAbsent(stock.getTimestamp(), k -> new HashMap<>())
                        .put(stock.getTicker(), stock);
                }
            }
        }

        // Calculate portfolio value at each timestamp
        List<PortfolioHistoryDTO> result = new ArrayList<>();
        
        for (Map.Entry<Instant, Map<String, Stock>> entry : dataByTimestamp.entrySet()) {
            Instant timestamp = entry.getKey();
            Map<String, Stock> stocksAtTime = entry.getValue();
            
            // Check if we have data for all holdings at this timestamp
            if (stocksAtTime.size() == holdingsByTicker.size()) {
                double totalValue = 0.0;
                boolean hasAllData = true;
                
                for (Map.Entry<String, Holding> holdingEntry : holdingsByTicker.entrySet()) {
                    String ticker = holdingEntry.getKey();
                    Holding holding = holdingEntry.getValue();
                    Stock stock = stocksAtTime.get(ticker);
                    
                    if (stock == null) {
                        hasAllData = false;
                        break;
                    }
                    
                    totalValue += stock.getCurrentPrice() * holding.getShares();
                }
                
                if (hasAllData) {
                    result.add(new PortfolioHistoryDTO(timestamp, totalValue));
                }
            }
        }
        
        // Sort by timestamp (oldest first for chart display)
        result.sort(Comparator.comparing(PortfolioHistoryDTO::getTimestamp));
        
        return result;
    }

}

