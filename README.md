### System Architecture

The system has been designed as a chain of one-to-one subscribers and publishers using a `java.util.concurrent.LinkedBlockingQueue` as the main data structure for reading and writing the events.

The `com.leedanieluk.PortfolioVisualizer.PortfolioVisualizer` subscribes to `com.leedanieluk.PortfolioValuation.PortfolioValuationPublisher` which subscribes to `com.leedanieluk.MarketData.MarketDataPublisher`.

The events of the system are:
* `com.leedanieluk.MarketData.MarketDataPriceUpdate`
* `com.leedanieluk.PortfolioValuation.PortfolioValuation`

`com.leedanieluk.MarketData.MarketDataPublisher` publishes the MarketDataPriceUpdate events.

`com.leedanieluk.PortfolioValuation.PortfolioValuationPublisher` publishes the PortfolioValuation events.

At the end of the pub/sub chain is the `com.leedanieluk.PortfolioVisualizer.PortfolioVisualizer` which subscribes to the PortfolioValuation event queue to print the "pretty printed" view of the positions in real time.

Interface implementations of the main classes of the system:
* `com.leedanieluk.MarketData.MarketDataPublisher` implements `Publisher<List<MarketDataPriceUpdate>>`
* `com.leedanieluk.PortfolioValuation.PortfolioValuationPublisher` implements `Subscriber<List<MarketDataPriceUpdate>>` and `Publisher<PortfolioValuation>`
* `com.leedanieluk.PortfolioVisualizer.PortfolioVisualizer` implements `Subscriber<PortfolioValuation>`

### Enhancements
There are many things that could be improved given more time. Some of these are:
* Writing more tests (both unit and integration tests), specially around the Brownian Motion and Options pricing calculations.
* Adding good start/stop APIs for the Subscribers and Consumers to shut down systems gracefully and cleaning up resources as needed.

### External libraries used
* H2 database to store data in-memory
* Google Guava (to cache hot in-memory DB requests)
* JUnit (to write unit tests)
* Apache Commons Math (to compute Cumulative Probability function, asked recruiter for permission)
