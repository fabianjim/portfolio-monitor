package com.github.fabianjim.portfoliomonitor.controller;

import com.github.fabianjim.portfoliomonitor.model.Holding;
import com.github.fabianjim.portfoliomonitor.model.Portfolio;
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
    @PostMapping("/create")
    public void createPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.createPortfolio(portfolio);
    }

    // TODO
    @PostMapping("/update")
    public void updatePortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.updatePortfolio(portfolio);
    }

    @GetMapping
    public Portfolio getPortfolio() {
        return portfolioService.getPortfolio();
    }

    @GetMapping("/exists")
    public boolean exists() {
        return portfolioService.existsByUserId();
    }

    @GetMapping("/holdings")
    public List<Holding> fetchHoldings() {
        Portfolio current = portfolioService.getPortfolio();
        return current != null ? current.getHoldings() : List.of();
    }

}
