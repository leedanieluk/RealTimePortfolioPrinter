package com.leedanieluk;

import com.leedanieluk.MarketData.MarketDataPriceUpdate;
import com.leedanieluk.MarketData.MarketDataPublisher;
import com.leedanieluk.PortfolioValuation.PortfolioValuationPublisher;
import com.leedanieluk.PortfolioVisualizer.PortfolioVisualizer;
import com.leedanieluk.Security.SecurityRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * High-level approach (BQ = BlockingQueue)
 * (Market Data Publisher) -> BQ -> (Option Price Calculator) -> BQ -> (Portfolio Result Subscriber)
 */
public class Main {
    public static void main(String[] args) throws Exception, SQLException, InterruptedException {
        // Loads security definitions to in-memory H2 database
        SecurityRepository.loadSecurityDefinitions();

        // Start market data provider in a daemon thread
        Publisher<List<MarketDataPriceUpdate>> marketDataPublisher = new MarketDataPublisher();
        marketDataPublisher.start();

        // Start option portfolio valuation provider
        PortfolioValuationPublisher portfolioValuationPublisher = new PortfolioValuationPublisher();

        // Start portfolio valuation visualizer
        PortfolioVisualizer portfolioVisualizer = new PortfolioVisualizer();

        // Subscribe portfolio valuation provider to market data provider
        marketDataPublisher.subscribe(portfolioValuationPublisher);

        // Subscribe portfolio valuation visualizer to portfolio valuation provider
        portfolioValuationPublisher.subscribe(portfolioVisualizer);
    }
}