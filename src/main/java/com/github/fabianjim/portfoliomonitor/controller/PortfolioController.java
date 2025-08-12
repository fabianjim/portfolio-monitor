package com.github.fabianjim.portfoliomonitor.controller;

import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.service.Portfolio;
import com.github.fabianjim.portfoliomonitor.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }
    @PostMapping
    public void submitPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.updatePortfolio(portfolio);
    }

    @GetMapping("/holdings")
    public List<Holding> fetchHoldings() {
        Portfolio current = portfolioService.getCurrentPortfolio();
        return current != null ? current.getHoldings() : List.of();
    }



}
