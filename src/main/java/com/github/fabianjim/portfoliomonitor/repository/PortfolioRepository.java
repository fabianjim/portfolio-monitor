package com.github.fabianjim.portfoliomonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.fabianjim.portfoliomonitor.model.Portfolio;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    

    boolean existsByUserId(Integer userId);

    Optional<Portfolio> findByUserId(Integer userId);
    
}
