package org.poo.plans;

import static org.poo.utils.Constants.*;

public final class GoldPlan implements AccountPlan {
    @Override
    public double getCashbackRate(final double totalSpendingAmount) {
        if (totalSpendingAmount >= THIRD_THRESHOLD) {
            return GOLD_BIG_CASHBACK_RATE;
        } else if (totalSpendingAmount >= SECOND_THRESHOLD) {
            return GOLD_MEDIUM_CASHBACK_RATE;
        } else if (totalSpendingAmount >= FIRST_THRESHOLD) {
            return GOLD_SMALL_CASHBACK_RATE;
        }
        return 0.0;
    }

    @Override
    public String getPlanName() {
        return "gold";
    }
}
