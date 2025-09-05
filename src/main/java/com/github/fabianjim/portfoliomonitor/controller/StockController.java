package com.github.fabianjim.portfoliomonitor.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.service.PortfolioService;
import com.github.fabianjim.portfoliomonitor.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    

    private final StockService stockService;
    private final PortfolioService portfolioService;

    public StockController(StockService stockService, PortfolioService portfolioService) {
        this.stockService = stockService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/fetch")
    public List<Stock> fectchStocksForPortfolio() {
        Portfolio portfolio = portfolioService.getPortfolio();
        System.out.println("Portfolio from fetching before getTickers(): " + portfolio.getHoldings());
        List<String> tickers = portfolioService.getTickersfromPortfolio(portfolio);
        System.out.println("Tickers from fetching: " + tickers);
        return stockService.updateMultipleStocks(tickers);
    }
}
