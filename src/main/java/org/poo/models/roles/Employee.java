package org.poo.models.roles;

import org.poo.models.Account;

public final class Employee implements Role {
    @Override
    public boolean canSetLimits() {
        return false;
    }

    @Override
    public boolean canPerformTransaction(final double amount, final String type,
                                         final Account account) {
        if (type.equals("spending")) {
            return amount <= account.getSpendingLimit();
        }
        return amount <= account.getDepositLimit();
    }

    @Override
    public String getType() {
        return "employee";
    }
}
