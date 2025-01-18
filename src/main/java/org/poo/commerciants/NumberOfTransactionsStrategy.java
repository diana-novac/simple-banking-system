package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

public final class NumberOfTransactionsStrategy implements CashbackStrategy {
    @Override
    public boolean isEligible(final Account account, final String category) {
        if (!account.getRequiredTransactions().containsKey(category)) {
            System.out.println("Nu are discount la categoria " + category);
            return false;
        }
        return account.getNumTransactions() > account.getRequiredTransactions().get(category);
    }

    @Override
    public void applyCashback(final App app, final User user, final Account account,
                              final String category, final double transactionAmount) {
        account.setNumTransactions(account.getNumTransactions() + 1);
        System.out.println(user.getFirstName() + " este la tranzactia cu nr " + account.getNumTransactions());
        if (!isEligible(account, category)) {
            System.out.println("Nu are suficiente tranzactii ca sa aplicam discount pt " + category);
            return;
        }
        double cashbackRate = account.getDiscounts().get(category);
        double cashbackAmount = transactionAmount * cashbackRate;

        System.out.println("Are discount de " + cashbackRate + " la aceasta tranzactie.");
        account.addCashback(cashbackAmount, category);
    }
}
