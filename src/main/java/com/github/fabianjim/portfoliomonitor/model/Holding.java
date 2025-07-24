package com.github.fabianjim.portfoliomonitor.model;

public class Holding {
    private String ticker;
    private int shares;

    public Holding(String ticker, int shares) {
        this.ticker = ticker;
        this.shares = shares;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
