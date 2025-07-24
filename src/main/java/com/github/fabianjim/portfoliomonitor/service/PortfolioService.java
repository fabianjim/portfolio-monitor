package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.model.Holding;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {


    public List<Holding> getHoldings() {
        return List.of(
                new Holding("AAPL", 10),
                new Holding("NVDA", 5)
        );
    }
}
