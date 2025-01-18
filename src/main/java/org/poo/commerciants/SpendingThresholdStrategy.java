package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

import static org.poo.utils.Constants.FIRST_THRESHOLD;

public final class SpendingThresholdStrategy implements CashbackStrategy {
    @Override
    public boolean isEligible(final Account account, final String category) {
        return account.getSpendingAmount() >= FIRST_THRESHOLD;
    }

    @Override
    public void applyCashback(final App app, final User user, final Account account,
                              final String category, final double transactionAmount) {
        account.addSpendingAmount(transactionAmount);
        if (!isEligible(account, category)) {
            return;
        }
        double cashbackRate = user.getAccountPlan()
                .getCashbackRate(account.getSpendingAmount());
        System.out.println(user.getFirstName() + " primeste cashback acum.");
        System.out.println(cashbackRate);
        double cashbackAmount = transactionAmount * cashbackRate;
        account.addCashback(app, cashbackAmount);
    }
}
