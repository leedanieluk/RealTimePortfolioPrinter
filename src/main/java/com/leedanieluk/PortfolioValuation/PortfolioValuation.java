package com.leedanieluk.PortfolioValuation;

import java.util.HashMap;
import java.util.Map;

public class PortfolioValuation {
    private final Map<String, Double> securityPrices;
    private final Map<String, MarketValue> positionMarketValue;
    private double NAV;

    public PortfolioValuation() {
        this.securityPrices = new HashMap<>();
        this.positionMarketValue = new HashMap<>();
    }

    public Map<String, Double> getSecurityPrices() {
        return securityPrices;
    }

    public Map<String, MarketValue> getPositionMarketValue() {
        return positionMarketValue;
    }

    public double getNAV() {
        return NAV;
    }

    public void setNAV(double NAV) {
        this.NAV = NAV;
    }

    public static class MarketValue {
        private final double price;
        private final double qty;
        private final double value;

        public MarketValue(double price, double qty, double value) {
            this.price = price;
            this.qty = qty;
            this.value = value;
        }

        public double getPrice() {
            return price;
        }

        public double getQty() {
            return qty;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "MarketValue{" +
                    "price=" + price +
                    ", qty=" + qty +
                    ", value=" + value +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PortfolioValuation{" +
                "securityPrices=" + securityPrices +
                ", positionMarketValue=" + positionMarketValue +
                ", NAV=" + NAV +
                '}';
    }
}
