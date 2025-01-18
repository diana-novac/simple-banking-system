package org.poo.plans;

import org.poo.main.App;

import static org.poo.utils.Constants.*;

public interface AccountPlan {
    default double getTransactionFee(final App app, final double amount) {
        return 0.0;
    };

    default boolean automaticUpgrade(final int transactionCount) {
        return false;
    }
    default double getCashbackRate(final double totalSpendingAmount) {
        if (totalSpendingAmount >= THIRD_THRESHOLD) {
            return STANDARD_BIG_CASHBACK_RATE;
        } else if (totalSpendingAmount >= SECOND_THRESHOLD) {
            return STANDARD_MEDIUM_CASHBACK_RATE;
        } else if (totalSpendingAmount >= FIRST_THRESHOLD) {
            return STANDARD_SMALL_CASHBACK_RATE;
        }
        return 0.0;
    }

    String getPlanName();
}
