package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

public interface CashbackStrategy {
    boolean isEligible(Account account, String category);
    void applyCashback(App app, User user, Account account, String category,
                       double transactionAmount);
}
