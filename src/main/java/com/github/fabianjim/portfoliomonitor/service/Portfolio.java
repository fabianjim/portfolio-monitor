package com.github.fabianjim.portfoliomonitor.service;

import com.github.fabianjim.portfoliomonitor.model.Holding;

import java.util.List;

public class Portfolio {

    private List<Holding> holdings;

    public Portfolio() {};

    public Portfolio(List<Holding> holdings) {
        this.holdings = holdings;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings;
    }
}
