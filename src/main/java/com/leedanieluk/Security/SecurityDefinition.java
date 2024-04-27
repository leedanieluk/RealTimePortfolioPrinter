package com.leedanieluk.Security;

import java.time.LocalDate;

public class SecurityDefinition {
    private final String ticker;
    private final double expectedReturn;
    private final double annualizedStandardDeviation;

    public SecurityDefinition(String ticker, double expectedReturn, double annualizedStandardDeviation) {
        this.ticker = ticker;
        this.expectedReturn = expectedReturn;
        this.annualizedStandardDeviation = annualizedStandardDeviation;
    }

    public String getTicker() {
        return ticker;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getAnnualizedStandardDeviation() {
        return annualizedStandardDeviation;
    }

    public static class Stock extends SecurityDefinition {

        public Stock(String ticker, double expectedReturn, double annualizedStandardDeviation) {
            super(ticker, expectedReturn, annualizedStandardDeviation);
        }

        @Override
        public String toString() {
            return "Stock{" +
                    "ticker='" + super.ticker + '\'' +
                    ", expectedReturn=" + super.expectedReturn +
                    ", annualizedStandardDeviation=" + super.annualizedStandardDeviation +
                    '}';
        }
    }

    public static class Option extends SecurityDefinition {
        private final LocalDate maturity;
        private final double strike;
        private final Type type;

        public Option(String ticker, LocalDate maturity, double strike, double expectedReturn, double annualizedStandardDeviation, Type type) {
            super(ticker, expectedReturn, annualizedStandardDeviation);
            this.maturity = maturity;
            this.strike = strike;
            this.type = type;
        }

        public LocalDate getMaturity() {
            return maturity;
        }

        public double getStrike() {
            return strike;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            CALL, PUT
        }

        @Override
        public String toString() {
            return "Option{" +
                    "maturity=" + maturity +
                    ", strike=" + strike +
                    ", type=" + type +
                    ", ticker='" + super.ticker + '\'' +
                    ", expectedReturn=" + super.expectedReturn +
                    ", annualizedStandardDeviation=" + super.annualizedStandardDeviation +
                    '}';
        }
    }
}
