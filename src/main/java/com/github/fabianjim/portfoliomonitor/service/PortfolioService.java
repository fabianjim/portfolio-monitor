package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.model.TrackedStock;
import com.github.fabianjim.portfoliomonitor.model.User;
import com.github.fabianjim.portfoliomonitor.repository.PortfolioRepository;
import com.github.fabianjim.portfoliomonitor.repository.TrackedStockRepository;
import com.github.fabianjim.portfoliomonitor.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.Instant;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final TrackedStockRepository trackedStockRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                          StockService stockService,
                          UserRepository userRepository,
                          TrackedStockRepository trackedStockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;
        this.trackedStockRepository = trackedStockRepository;
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

}

