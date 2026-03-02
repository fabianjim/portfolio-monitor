package com.github.fabianjim.portfoliomonitor.dto;

import com.github.fabianjim.portfoliomonitor.model.Stock;

import java.time.Instant;

public class StockDataDTO {
    private Stock stock;
    private boolean stale;
    private String staleWarning;
    private Instant lastSuccessfulFetch;

    public StockDataDTO(Stock stock, boolean stale, String staleWarning, Instant lastSuccessfulFetch) {
        this.stock = stock;
        this.stale = stale;
        this.staleWarning = staleWarning;
        this.lastSuccessfulFetch = lastSuccessfulFetch;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean stale) {
        this.stale = stale;
    }

    public String getStaleWarning() {
        return staleWarning;
    }

    public void setStaleWarning(String staleWarning) {
        this.staleWarning = staleWarning;
    }

    public Instant getLastSuccessfulFetch() {
        return lastSuccessfulFetch;
    }

    public void setLastSuccessfulFetch(Instant lastSuccessfulFetch) {
        this.lastSuccessfulFetch = lastSuccessfulFetch;
    }
}
