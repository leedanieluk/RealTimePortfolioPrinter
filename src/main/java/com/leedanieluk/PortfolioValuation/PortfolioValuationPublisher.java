package com.leedanieluk.PortfolioValuation;

import com.leedanieluk.MarketData.MarketDataPriceUpdate;
import com.leedanieluk.OptionPricing.OptionPricing;
import com.leedanieluk.OptionPricing.OptionPricingService;
import com.leedanieluk.Portfolio.PortfolioPosition;
import com.leedanieluk.Portfolio.PortfolioPositionRepository;
import com.leedanieluk.Publisher;
import com.leedanieluk.Subscriber;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class PortfolioValuationPublisher implements Subscriber<List<MarketDataPriceUpdate>>, Publisher<PortfolioValuation> {
    private final LinkedBlockingDeque<PortfolioValuation> portfolioValuations;
    private final OptionPricingService optionPricingService;

    public PortfolioValuationPublisher() {
        this.portfolioValuations = new LinkedBlockingDeque<>();
        this.optionPricingService = new OptionPricingService();
    }

    @Override
    public void start() {
    }

    @Override
    public void accept(List<MarketDataPriceUpdate> marketDataPriceUpdates) {
        try {
            List<OptionPricing> optionPricingUpdates = optionPricingService.calculatePricing(marketDataPriceUpdates);
            Map<String, Map<OptionPricing.Type, List<OptionPricing>>> optionPricingByTicker = optionPricingUpdates.stream().collect(Collectors.groupingBy(OptionPricing::getTicker, Collectors.groupingBy(OptionPricing::getType)));
            List<PortfolioPosition> positions = PortfolioPositionRepository.getPositionsFromFile("positions.csv");
            PortfolioValuation portfolioValuation = new PortfolioValuation();
            Map<String, Double> securityPrices = portfolioValuation.getSecurityPrices();
            for (MarketDataPriceUpdate priceUpdate : marketDataPriceUpdates) {
                securityPrices.put(priceUpdate.getTicker(), priceUpdate.getPrice());
            }
            double nav = 0;
            for (PortfolioPosition position : positions) {
                String symbol = position.getSymbol();
                double size = position.getSize();
                double price;
                if (symbol.contains("-")) {
                    String[] optionParams = symbol.split("-");
                    if (optionParams[4].equals("C")) {
                        price = optionPricingByTicker.get(optionParams[0]).get(OptionPricing.Type.CALL).get(0).getPrice();
                    } else {
                        price = optionPricingByTicker.get(optionParams[0]).get(OptionPricing.Type.PUT).get(0).getPrice();
                    }
                } else {
                    price = securityPrices.get(symbol);
                }
                nav += price * size;
                portfolioValuation.getPositionMarketValue().put(symbol, new PortfolioValuation.MarketValue(price, size, price * size));
            }
            portfolioValuation.setNAV(nav);
            portfolioValuations.put(portfolioValuation);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(Subscriber<PortfolioValuation> subscriber) {
        new Thread(() -> {
            while (true) {
                try {
                    subscriber.accept(portfolioValuations.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
