package com.github.fabianjim.portfoliomonitor.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import com.github.fabianjim.portfoliomonitor.model.Portfolio;
import com.github.fabianjim.portfoliomonitor.model.Stock;
import com.github.fabianjim.portfoliomonitor.model.Stock.StockType;
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

    // initial manual fetch from the frontend before routine fetches
    @GetMapping("/fetch/initial")
    public List<Stock> fectchInitialStocks() {
        Portfolio portfolio = portfolioService.getPortfolio();
        List<String> tickers = portfolioService.getTickersfromPortfolio(portfolio);
        return stockService.updateMultipleStocks(tickers, StockType.INITIAL, portfolio);
    }

    @GetMapping("/fetch/intraday")
    public List<Stock> fetchIntradayStocks() {
        return null;
    }

    @GetMapping("/fetch/eod")
    public List<Stock> fetchEODStocks() {
        return null;
    }
}
