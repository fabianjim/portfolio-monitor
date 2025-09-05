package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.model.User;
import com.github.fabianjim.portfoliomonitor.repository.PortfolioRepository;
import com.github.fabianjim.portfoliomonitor.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                          StockService stockService,
                          UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;
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
        }
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

    public Stock getStockData(String ticker, String timestamp) {
        return stockService.getStockData(ticker, timestamp).orElse(null);
    }

}

