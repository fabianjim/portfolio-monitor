package com.github.fabianjim.portfoliomonitor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "holdings")
public class Holding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String ticker;
    
    @Column(nullable = false)
    private double shares;
    
    @Column(name = "average_cost", nullable = false)
    private double averageCost;

    public Holding() {}

    public Holding(String ticker, double shares, double averageCost) {
        this.ticker = ticker;
        this.shares = shares;
        this.averageCost = averageCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getShares() {
        return shares;
    }

    public void setShares(double shares) {
        this.shares = shares;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }
}
