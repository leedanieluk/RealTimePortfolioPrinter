package com.leedanieluk.Portfolio;

public class PortfolioPosition {
    private final String symbol;
    private final Long size;

    public PortfolioPosition(String symbol, Long size) {
        this.symbol = symbol;
        this.size = size;
    }

    public static PortfolioPosition fromString(String positionAsString) {
        String[] position = positionAsString.split(",");
        return new PortfolioPosition(position[0], Long.parseLong(position[1]));
    }

    public String getSymbol() {
        return symbol;
    }

    public Long getSize() {
        return size;
    }
}
