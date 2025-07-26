package com.github.fabianjim.portfoliomonitor.service;

import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    public Portfolio currentPortfolio;

    public void updatePortfolio(Portfolio portfolio) {
        this.currentPortfolio = portfolio;
    }

    public Portfolio getCurrentPortfolio() {
        return currentPortfolio;
    }
}
