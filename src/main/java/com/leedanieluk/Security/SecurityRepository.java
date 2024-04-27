package com.leedanieluk.Security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.leedanieluk.Portfolio.PortfolioPosition;
import com.leedanieluk.Portfolio.PortfolioPositionRepository;
import com.leedanieluk.util.DateUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SecurityRepository {
    private final static String CONNECTION_URL = "jdbc:h2:mem:security;DB_CLOSE_DELAY=-1";

    private static LoadingCache<String, SecurityDefinition.Stock> stockDefinitionsCache;
    private static LoadingCache<String, List<SecurityDefinition.Option>> optionsDefinitionsCache;
    static {
        CacheLoader<String, SecurityDefinition.Stock> stocksDefinitionsLoader = new CacheLoader<String, SecurityDefinition.Stock>() {
            @Override
            public SecurityDefinition.Stock load(String ticker) {
                try {
                    return getStockDefinitionsForTicker(ticker);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        stockDefinitionsCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(stocksDefinitionsLoader);
        CacheLoader<String, List<SecurityDefinition.Option>> optionDefinitionsLoader = new CacheLoader<String, List<SecurityDefinition.Option>>() {
            @Override
            public List<SecurityDefinition.Option> load(String ticker) {
                try {
                    return getOptionsDefinitionsForTicker(ticker);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        optionsDefinitionsCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(optionDefinitionsLoader);
    }

    public static void loadSecurityDefinitions() throws SQLException, IOException {
        List<PortfolioPosition> positions = PortfolioPositionRepository.getPositionsFromFile("positions.csv");
        try (Connection con = DriverManager.getConnection(CONNECTION_URL);
             Statement stm = con.createStatement()) {
            // create tables for each security type (stock, call and put).
            // Assumption: list of positions contain a unique ticker per security type (identifier)
            String stockSql = "CREATE TABLE STOCK " +
                    "(ticker VARCHAR(255) NOT NULL, " +
                    "expected_return FLOAT NOT NULL, " +
                    "annualized_standard_deviation FLOAT NOT NULL, " +
                    "PRIMARY KEY ( ticker ))";
            stm.executeUpdate(stockSql);
            String callSql = "CREATE TABLE CALL " +
                    "(ticker VARCHAR(255) NOT NULL, " +
                    "maturity VARCHAR(255) NOT NULL, " +
                    "strike FLOAT NOT NULL, " +
                    "PRIMARY KEY ( ticker ))";
            stm.executeUpdate(callSql);
            String putSql = "CREATE TABLE PUT " +
                    "(ticker VARCHAR(255) NOT NULL, " +
                    "maturity VARCHAR(255) NOT NULL," +
                    "strike FLOAT NOT NULL, " +
                    "PRIMARY KEY ( ticker ))";
            stm.executeUpdate(putSql);

            // extract security attributes from list of positions
            for (PortfolioPosition position : positions) {
                String symbol = position.getSymbol();
                if (symbol.contains("-")) {
                    String[] optionParameters = symbol.split("-");
                    String ticker = optionParameters[0];
                    String maturity = optionParameters[1] + "-" + optionParameters[2];
                    String optionType = optionParameters[4];
                    double strike = Double.parseDouble(optionParameters[3]);
                    if (optionType.equals("C")) {
                        stm.executeUpdate(String.format("INSERT INTO Call VALUES ('%s', '%s', %f)", ticker, maturity, strike));
                    } else {
                        stm.executeUpdate(String.format("INSERT INTO Put VALUES ('%s', '%s', %f)", ticker, maturity, strike));
                    }
                } else {
                    stm.executeUpdate(String.format("INSERT INTO Stock VALUES ('%s', %f, %f)", symbol, Math.random(), Math.random()));
                }
            }
        }
    }

    public static SecurityDefinition.Stock getCachedStockDefinitionsForTicker(String ticker) throws ExecutionException {
        return stockDefinitionsCache.get(ticker);
    }

    public static List<SecurityDefinition.Option> getCachedOptionsDefinitionsForTicker(String ticker) throws ExecutionException {
        return optionsDefinitionsCache.get(ticker);
    }

    public static SecurityDefinition.Stock getStockDefinitionsForTicker(String ticker) throws SQLException {
        try (Connection con = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement stockStm = con.prepareStatement("SELECT * FROM STOCK WHERE ticker = ?")) {
            stockStm.setString(1, ticker);
            try (ResultSet stockResultSet = stockStm.executeQuery()) {
                stockResultSet.next();
                return new SecurityDefinition.Stock(ticker, stockResultSet.getDouble("expected_return"), stockResultSet.getDouble("annualized_standard_deviation"));
            }
        }
    }

    public static List<SecurityDefinition.Option> getOptionsDefinitionsForTicker(String ticker) throws SQLException {
        try (Connection con = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement stockStm = con.prepareStatement("SELECT * FROM STOCK WHERE ticker = ?");
             PreparedStatement callStm = con.prepareStatement("SELECT * FROM CALL WHERE ticker = ?");
             PreparedStatement putStm = con.prepareStatement("SELECT * FROM PUT WHERE ticker = ?")) {
            stockStm.setString(1, ticker);
            callStm.setString(1, ticker);
            putStm.setString(1, ticker);
            List<SecurityDefinition.Option> optionsDefinitions = new ArrayList<>();
            try (ResultSet stockResultSet = stockStm.executeQuery();
                 ResultSet callResultSet = callStm.executeQuery();
                 ResultSet putResultSet = putStm.executeQuery()) {
                stockResultSet.next(); callResultSet.next(); putResultSet.next();
                optionsDefinitions.add(new SecurityDefinition.Option(ticker, DateUtil.parseDate(callResultSet.getString("maturity")), callResultSet.getDouble("strike"), stockResultSet.getDouble("expected_return"), stockResultSet.getDouble("annualized_standard_deviation"), SecurityDefinition.Option.Type.CALL));
                optionsDefinitions.add(new SecurityDefinition.Option(ticker, DateUtil.parseDate(putResultSet.getString("maturity")), callResultSet.getDouble("strike"), stockResultSet.getDouble("expected_return"), stockResultSet.getDouble("annualized_standard_deviation"), SecurityDefinition.Option.Type.PUT));
            }
            return optionsDefinitions;
        }
    }
}
