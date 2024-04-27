package com.leedanieluk.PortfolioVisualizer;

import com.leedanieluk.PortfolioValuation.PortfolioValuation;
import com.leedanieluk.Subscriber;

import java.util.HashMap;
import java.util.Map;

public class PortfolioVisualizer implements Subscriber<PortfolioValuation> {
    private static int counter = 0;
    private final static Map<String, String> previousPrices = new HashMap<>();

    @Override
    public void accept(PortfolioValuation portfolioValuation) {
        Map<String, Double> securityPrices = portfolioValuation.getSecurityPrices();
        Map<String, PortfolioValuation.MarketValue> positionsMarketValue =  portfolioValuation.getPositionMarketValue();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("## %d Market Data Update", ++counter));
        sb.append("\n");
        securityPrices.keySet().forEach(ticker -> {
            double price = securityPrices.get(ticker);
            if (previousPrices.containsKey(ticker)) {
                if (!previousPrices.get(ticker).equals(String.format("%.2f", price))) {
                    sb.append(String.format("%s change to %.2f", ticker, price));
                    sb.append("\n");
                    previousPrices.put(ticker, String.format("%.2f", price));
                }
            } else {
                sb.append(String.format("%s change to %.2f", ticker, price));
                sb.append("\n");
                previousPrices.put(ticker, String.format("%.2f", price));
            }
        });
        sb.append("\n");
        sb.append("## Portfolio");
        sb.append("\n");
        sb.append(String.format("%s\t%s\t%s\t%s", addPadding("symbol", 20, false), addPadding("price", 20, true), addPadding("qty", 20, true), addPadding("value", 20, true)));
        sb.append("\n");
        positionsMarketValue.keySet().forEach(symbol -> {
            PortfolioValuation.MarketValue marketValue = positionsMarketValue.get(symbol);
            String price = String.format("%.2f", marketValue.getPrice());
            String value = String.format("%.2f", marketValue.getValue()).equals("-0.00") ? "0.00" : String.format("%.2f", marketValue.getValue());
            sb.append(String.format("%s\t%s\t%s\t%s", addPadding(symbol, 20, false), addPadding(price, 20, true), addPadding(String.format("%.2f", marketValue.getQty()), 20, true), addPadding(value, 20 ,true)));
            sb.append("\n");
        });
        sb.append("\n");
        sb.append(String.format("#Total portfolio\t%.2f", portfolioValuation.getNAV()));
        sb.append("\n"); sb.append("\n");
        System.out.println(sb);
    }

    public String addPadding(String input, int length, boolean left) {
        StringBuilder sb = new StringBuilder();
        if (left) {
            for (int i = 0; i < length - input.length(); i++) {
                sb.append(" ");
            }
            sb.append(input);
        } else {
            sb.append(input);
            for (int i = 0; i < length - input.length(); i++) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
