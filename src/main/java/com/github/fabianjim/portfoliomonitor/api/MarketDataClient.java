package com.github.fabianjim.portfoliomonitor.api;

import com.github.fabianjim.portfoliomonitor.model.Stock;

public interface MarketDataClient {
    /**
     * Fetch current price of the associated ticker stock
     * @param ticker symbol
     * @return current ticker price for one share
     */
    double getCurrentPrice(String ticker);

    /**
     * Sends request to API to test connection
     * @return string verifying successful authentication and connection
     */
    String testConnection();

    /**
     * Fetch detailed stock data for the ticker passed from the market API
     * @param ticker listed for the stock
     * @return Stock object with data retrieval timestamp, current price, day opening price, previous day's closing price,
     * highest price today, and lowest price today of the stock
     */
    Stock getStockData(String ticker);
}
