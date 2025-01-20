package org.poo.models.roles;

import org.poo.models.Account;

public final class Manager implements Role {
    @Override
    public boolean canSetLimits() {
        return false;
    }

    @Override
    public boolean canPerformTransaction(final double amount, final String type,
                                         final Account account) {
        return true;
    }

    @Override
    public String getType() {
        return "manager";
    }
}
