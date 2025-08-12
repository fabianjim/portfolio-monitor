package com.github.fabianjim.portfoliomonitor.model;

public class Stock {

    private String ticker;
    private String timestamp;
    private double currentPrice;
    private double open;
    private double prevClose;
    private double high;
    private double low;
    //TODO: Implement shares. User must input their number of shares owned

    public Stock() {};
    public Stock(String ticker, String timestamp, double currentPrice, double open, double prevClose, double high, double low) {
        this.ticker = ticker;
        this.timestamp = timestamp;
        this.currentPrice = currentPrice;
        this.open = open;
        this.prevClose = prevClose;
        this.high = high;
        this.low = low;
    };


    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getHigh() {
        return high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getLow() {
        return low;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getOpen() {
        return open;
    }
    public void setPrevClose(double prevClose) {
        this.prevClose = prevClose;
    }

    public double getPrevClose() {
        return prevClose;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
