package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

import java.util.Map;

/**
 * Cashback strategy implementation based on the number of transactions
 * This strategy checks if an account has performed a sufficient number of transactions
 * in a specific category to be eligible for cashback rewards
 */
public final class NumberOfTransactionsStrategy implements CashbackStrategy {
    @Override
    public boolean isEligible(final App app, final User user,
                              final Account account, final String category) {
        // Check if the account may still get a discount for the category
        if (!account.getRequiredTransactions().containsKey(category)) {
            return false;
        }

        // Iterate through all commerciants to find those using this strategy
        for (Commerciant commerciant : app.getCommerciants()) {
            if (commerciant.getStrategy().getType().equals("nrOfTransactions")) {
                // Check if the account has a sufficient number of transactions
                if (commerciant.getAccountNumTransactions().containsKey(account.getIban())) {
                    if (commerciant.getAccountNumTransactions()
                            .get(account.getIban()) > account.getRequiredTransactions()
                            .get(category)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void applyCashback(final App app, final User user, final Account account,
                              final String category, final double transactionAmount,
                              final Commerciant commerciant) {
        // Increment the number of transactions made by this account for the commerciant
        Map<String, Integer> numTransactions = commerciant.getAccountNumTransactions();
        numTransactions.put(account.getIban(),
                numTransactions.getOrDefault(account.getIban(), 0) + 1);

        if (!isEligible(app, user, account, category)) {
            return;
        }

        // Calculate the cashback amount based on the discount rate
        double cashbackRate = account.getDiscounts().get(category);
        double cashbackAmount = transactionAmount * cashbackRate;

        // Add the cashback to the account
        account.addCashback(app, cashbackAmount, category);
    }

    @Override
    public String getType() {
        return "nrOfTransactions";
    }
}
