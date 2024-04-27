package com.leedanieluk.OptionPricing;

public class OptionPricing {
    private final String ticker;
    private final double price;
    private final Type type;

    public OptionPricing(String ticker, double price, Type type) {
        this.ticker = ticker;
        this.price = price;
        this.type = type;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CALL, PUT
    }

    @Override
    public String toString() {
        return "OptionPricing{" +
                "ticker='" + ticker + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
