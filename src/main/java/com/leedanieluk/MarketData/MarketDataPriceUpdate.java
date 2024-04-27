package com.leedanieluk.MarketData;

public class MarketDataPriceUpdate {
    private final String ticker;
    private final double price;

    public MarketDataPriceUpdate(String ticker, double price) {
        this.ticker = ticker;
        this.price = price;
    }

    public final String getTicker() {
        return ticker;
    }

    public final double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MarketDataPriceUpdate{" +
                "ticker='" + ticker + '\'' +
                ", price=" + price +
                '}';
    }
}
