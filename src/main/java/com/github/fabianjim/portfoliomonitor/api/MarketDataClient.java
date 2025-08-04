package com.github.fabianjim.portfoliomonitor.api;

public interface MarketDataClient {
    /**
     * Fetch current price of the associated ticker stock
     * @param ticker symbol
     * @return current ticker price for one share
     */
    double getPrice(String ticker);

    /**
     * Sends request to API to test connection
     * @return string verifying successful authentication and connection
     */
    String testConnection();

    /**
     * TODO: getHistoricalPrice(String ticker)
     */
}
