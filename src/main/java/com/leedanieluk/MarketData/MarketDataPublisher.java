package com.leedanieluk.MarketData;

import com.leedanieluk.Portfolio.PortfolioPositionRepository;
import com.leedanieluk.Publisher;
import com.leedanieluk.Security.SecurityDefinition;
import com.leedanieluk.Security.SecurityRepository;
import com.leedanieluk.Subscriber;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class MarketDataPublisher implements Publisher<List<MarketDataPriceUpdate>> {
    private static final Random random = new Random();
    private final LinkedBlockingDeque<List<MarketDataPriceUpdate>> priceUpdates;
    private double lastUpdate;
    private final Map<String, Double> stockPrices;
    private volatile boolean started = false;

    public MarketDataPublisher() {
        this.priceUpdates = new LinkedBlockingDeque<>();
        this.stockPrices = new HashMap<>();
    }

    public void start() throws IOException {
        if (!started) {
            started = true;

            // set initial stock prices with random values
            List<String> tickers = PortfolioPositionRepository.getUniqueTickersFromPortfolio("positions.csv");
            for (String ticker : tickers) {
                stockPrices.put(ticker, Math.random() * 100 + 100);
            }

            // start market data publisher
            Thread t = new Thread(() -> {
                List<MarketDataPriceUpdate> prices = new ArrayList<>(tickers.size());
                lastUpdate = System.currentTimeMillis();
                while (true) {
                    prices.clear();
                    try {
                        for (String ticker : tickers) {
                            double newPrice = computeBrownianMotionPrice(ticker);
                            stockPrices.put(ticker, newPrice);
                            prices.add(new MarketDataPriceUpdate(ticker, newPrice));
                        }
                        lastUpdate = System.currentTimeMillis();
                        priceUpdates.put(prices);
                        TimeUnit.MILLISECONDS.sleep(500 + (long) (Math.random() * 1500)); // 0.5s to 2s timed-wait
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    @Override
    public void subscribe(Subscriber<List<MarketDataPriceUpdate>> subscriber) {
        new Thread(() -> {
            while (true) {
                try {
                    subscriber.accept(priceUpdates.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private double computeBrownianMotionPrice(String ticker) throws SQLException, ExecutionException {
        SecurityDefinition.Stock securityDefinition = SecurityRepository.getCachedStockDefinitionsForTicker(ticker);
        double mu = securityDefinition.getExpectedReturn();
        double sigma = securityDefinition.getAnnualizedStandardDeviation();
        double epsilon = random.nextGaussian();
        double deltaT = (System.currentTimeMillis() - lastUpdate) / 1_000;
        double constant = 7_257_600;
        double S = stockPrices.get(ticker);
        double deltaS = (mu * (deltaT / constant) + sigma * epsilon * Math.sqrt(deltaT / constant)) * S;
        return S + deltaS;
    }
}
