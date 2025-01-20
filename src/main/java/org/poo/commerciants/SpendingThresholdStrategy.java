package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

import static org.poo.utils.Constants.FIRST_THRESHOLD;

/**
 * Cashback strategy implementation based on spending thresholds
 * This strategy awards cashback once an account's spending amount reaches a certain threshold
 */
public final class SpendingThresholdStrategy implements CashbackStrategy {
    @Override
    public boolean isEligible(final App app, final User user,
                              final Account account, final String category) {
        // Check if the total spending amount meets the defined threshold
        return account.getSpendingAmount() >= FIRST_THRESHOLD;
    }

    @Override
    public void applyCashback(final App app, final User user, final Account account,
                              final String category, final double transactionAmount,
                              final Commerciant commerciant) {
        // Increment the total spending amount for the account
        account.addSpendingAmount(transactionAmount);

        // Check if cashback can be awarded based on required transactions for the category
        if (account.getRequiredTransactions().containsKey(category)) {
            if (commerciant.getAccountNumTransactions().containsKey(account.getIban())) {
                if (commerciant.getAccountNumTransactions()
                        .get(account.getIban()) > account.getRequiredTransactions().get(category)) {
                    // Calculate cashback based on the category-specific discount
                    double cashbackRate = account.getDiscounts().get(category);
                    double cashbackAmount = transactionAmount * cashbackRate;
                    account.addCashback(app, cashbackAmount, category);
                    return;
                }
            }
        }

        // If not eligible based on category discount, check eligibility
        // based on spending thresholds
        if (!isEligible(app, user, account, category)) {
            return;
        }

        // Calculate cashback based on the user's account plan and spending amount
        double cashbackRate = user.getAccountPlan()
                .getCashbackRate(account.getSpendingAmount());
        double cashbackAmount = transactionAmount * cashbackRate;
        account.addCashback(app, cashbackAmount);
    }

    @Override
    public String getType() {
        return "spendingThreshold";
    }
}
