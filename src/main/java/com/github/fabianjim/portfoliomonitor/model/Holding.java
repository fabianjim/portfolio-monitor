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

    public Holding() {}

    public Holding(String ticker, double shares) {
        this.ticker = ticker;
        this.shares = shares;
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

}