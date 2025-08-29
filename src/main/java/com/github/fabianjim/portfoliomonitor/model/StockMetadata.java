package com.github.fabianjim.portfoliomonitor.model;

public class StockMetadata {
    
    private String ticker;
    private String name;
    private String country;
    private String sector;
    private String industry;

    public StockMetadata() {}

    public StockMetadata(String ticker, String name, String country, String sector, String industry) {
        this.ticker = ticker;
        this.name = name;
        this.country = country;
        this.sector = sector;
        this.industry = industry;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
