package org.poo.commerciants;

import org.poo.fileio.CommerciantInput;

public final class StrategyFactory {
    private StrategyFactory() { }

    public static CashbackStrategy createCashbackStrategy(final CommerciantInput input) {
        return switch (input.getCashbackStrategy()) {
            case "spendingThreshold" -> new SpendingThresholdStrategy();
            case "nrOfTransactions" -> new NumberOfTransactionsStrategy();
            default -> throw new IllegalArgumentException();
        };
    }
}
