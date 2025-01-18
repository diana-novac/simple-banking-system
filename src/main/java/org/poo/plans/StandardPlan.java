package org.poo.plans;

import org.poo.main.App;

import static org.poo.utils.Constants.STANDARD_FEE;

public final class StandardPlan implements AccountPlan {
    @Override
    public double getTransactionFee(final App app, final double amount) {
        return STANDARD_FEE;
    }

    @Override
    public String getPlanName() {
        return "standard";
    }
}
