package org.poo.commerciants;

import lombok.Data;
import org.poo.fileio.CommerciantInput;

@Data
public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private CashbackStrategy strategy;

    public Commerciant(final CommerciantInput input) {
        commerciant = input.getCommerciant();
        id = input.getId();
        account = input.getAccount();
        type = input.getType();
        strategy = StrategyFactory.createCashbackStrategy(input);
    }
}
