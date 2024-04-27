package com.leedanieluk.Portfolio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PortfolioPositionRepository {
    public static List<PortfolioPosition> getPositionsFromFile(String filename) throws IOException {
        File file = new File(Objects.requireNonNull(PortfolioPositionRepository.class.getClassLoader().getResource(filename)).getFile());
        try (Stream<String> positionsAsString = Files.lines(Paths.get(file.getAbsolutePath()))) {
            return positionsAsString.skip(1).map(PortfolioPosition::fromString).collect(Collectors.toList());
        }
    }

    public static List<String> getUniqueTickersFromPortfolio(String filename) throws IOException {
        List<PortfolioPosition> positions = getPositionsFromFile(filename);
        return positions.stream().map(position -> position.getSymbol().contains("-") ? position.getSymbol().split("-")[0] : position.getSymbol()).distinct().collect(Collectors.toList());
    }
}
