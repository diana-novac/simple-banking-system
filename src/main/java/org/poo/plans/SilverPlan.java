package org.poo.plans;

import org.poo.main.App;

import static org.poo.utils.Constants.*;

public final class SilverPlan implements AccountPlan {
    @Override
    public double getTransactionFee(final App app, final double amount) {
        if (amount < SILVER_NO_FEE_THRESHOLD) {
            return 0.0;
        }
        return SILVER_FEE;
    }

    @Override
    public boolean automaticUpgrade(final int transactionCount) {
        return transactionCount >= AUTOMATIC_UPGRADE_TRANSACTIONS;
    }

    @Override
    public double getCashbackRate(final double totalSpendingAmount) {
        if (totalSpendingAmount >= THIRD_THRESHOLD) {
            return SILVER_BIG_CASHBACK_RATE;
        } else if (totalSpendingAmount >= SECOND_THRESHOLD) {
            return SILVER_MEDIUM_CASHBACK_RATE;
        } else if (totalSpendingAmount >= FIRST_THRESHOLD) {
            return SILVER_SMALL_CASHBACK_RATE;
        }
        return 0.0;
    }

    @Override
    public String getPlanName() {
        return "silver";
    }
}
