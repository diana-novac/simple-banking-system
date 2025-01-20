package org.poo.commerciants;

import lombok.Data;
import org.poo.fileio.CommerciantInput;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a commerciant in the system
 * Stores information about the commerciant's account, type, cashback strategy,
 * and transaction statistics
 */
@Data
public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private CashbackStrategy strategy;

    /**
     * Tracks the number of transactions made by different accounts for this commerciant
     */
    private Map<String, Integer> accountNumTransactions;

    public Commerciant(final CommerciantInput input) {
        commerciant = input.getCommerciant();
        id = input.getId();
        account = input.getAccount();
        type = input.getType();
        strategy = StrategyFactory.createCashbackStrategy(input);
        accountNumTransactions = new HashMap<>();
    }
}
