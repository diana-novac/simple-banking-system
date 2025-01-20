package org.poo.commerciants;

import org.poo.fileio.CommerciantInput;

/**
 * Factory class for creating cashback strategies
 */
public final class StrategyFactory {

    /**
     * Private constructor to prevent instantiation
     */
    private StrategyFactory() { }

    /**
     * Creates a CashbackStrategy instance based on the cashback strategy type
     * specified in the input
     *
     * @param input The input data containing the cashback strategy type
     * @return A specific implementation of the CashbackStrategy interface
     * @throws IllegalArgumentException if the specified cashback strategy type is invalid
     */
    public static CashbackStrategy createCashbackStrategy(final CommerciantInput input) {
        return switch (input.getCashbackStrategy()) {
            case "spendingThreshold" -> new SpendingThresholdStrategy();
            case "nrOfTransactions" -> new NumberOfTransactionsStrategy();
            default -> throw new IllegalArgumentException();
        };
    }
}
