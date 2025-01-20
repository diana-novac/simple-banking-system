package org.poo.models.roles;

import org.poo.models.Account;

public final class Owner implements Role {
    @Override
    public boolean canSetLimits() {
        return true;
    }

    @Override
    public boolean canPerformTransaction(final double amount, final String type,
                                         final Account account) {
        return true;
    }

    @Override
    public String getType() {
        return "owner";
    }
}
