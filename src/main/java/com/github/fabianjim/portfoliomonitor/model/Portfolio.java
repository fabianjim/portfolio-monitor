package com.github.fabianjim.portfoliomonitor.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfolio_id")
    private List<Holding> holdings;

    public Portfolio() {};

    public Portfolio(User user, List<Holding> holdings) {
        this.user = user;
        this.holdings = holdings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
