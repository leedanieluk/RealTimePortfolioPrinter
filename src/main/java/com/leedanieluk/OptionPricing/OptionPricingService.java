package com.leedanieluk.OptionPricing;

import com.leedanieluk.MarketData.MarketDataPriceUpdate;
import com.leedanieluk.Security.SecurityDefinition;
import com.leedanieluk.Security.SecurityRepository;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OptionPricingService {

    public List<OptionPricing> calculatePricing(List<MarketDataPriceUpdate> marketDataPriceUpdates) {
        List<OptionPricing> updates = new ArrayList<>();
        for (MarketDataPriceUpdate priceUpdate : marketDataPriceUpdates) {
            try {
                List<SecurityDefinition.Option> optionsDefinitions = SecurityRepository.getCachedOptionsDefinitionsForTicker(priceUpdate.getTicker());

                for (SecurityDefinition.Option definition : optionsDefinitions) {
                    if (definition.getType() == SecurityDefinition.Option.Type.CALL) {
                        double callPrice = calculateCallPrice(priceUpdate.getPrice(), definition.getMaturity(), definition.getStrike(), definition.getExpectedReturn(), definition.getAnnualizedStandardDeviation());
                        updates.add(new OptionPricing(definition.getTicker(), callPrice, OptionPricing.Type.CALL));
                    }
                    if (definition.getType() == SecurityDefinition.Option.Type.PUT) {
                        double putPrice = calculatePutPrice(priceUpdate.getPrice(), definition.getMaturity(), definition.getStrike(), definition.getExpectedReturn(), definition.getAnnualizedStandardDeviation());
                        updates.add(new OptionPricing(definition.getTicker(), putPrice, OptionPricing.Type.PUT));
                    }
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return updates;

    }

    private double calculateCallPrice(double price, LocalDate maturity, double strike, double expectedReturn, double annualizedStandardDeviation) {
        double S = price;
        double K = strike;
        double r = 0.02;
        double t = Duration.between(LocalDate.now().atTime(0,0), maturity.atTime(0,0)).toDays() / (double) LocalDate.now().lengthOfYear();
        double mu = expectedReturn;
        double sigma = annualizedStandardDeviation;
        double d1 = calculateD1(S, K, sigma, t, r);
        double d2 = calculateD2(d1, sigma, t);
        return S * N(d1, mu, sigma) - K * Math.pow(Math.E, -r*t) * N(d2, mu, sigma);
    }

    private double calculatePutPrice(double price, LocalDate maturity, double strike, double expectedReturn, double annualizedStandardDeviation) {
        double S = price;
        double K = strike;
        double r = 0.02;
        double t = Duration.between(LocalDate.now().atTime(0,0), maturity.atTime(0,0)).toDays() / (double) LocalDate.now().lengthOfYear();
        double mu = expectedReturn;
        double sigma = annualizedStandardDeviation;
        double d1 = calculateD1(S, K, sigma, t, r);
        double d2 = calculateD2(d1, sigma, t);
        return K * Math.pow(Math.E, - r * t) * N(-d2, mu, sigma) - S * N(-d1, mu, sigma);
    }

    private double calculateD1(double S, double K, double sigma, double t, double r) {
        return (Math.log(S / K) + (r + Math.pow(sigma, 2) / 2) * t) / (sigma * Math.sqrt(t));
    }

    private double calculateD2(double d1, double sigma, double t) {
        return d1 - sigma * Math.sqrt(t);
    }

    public double N(double x, double mu, double sigma)  {
        NormalDistribution n = new NormalDistribution(mu, sigma);
        return n.cumulativeProbability(x);
    }
}
