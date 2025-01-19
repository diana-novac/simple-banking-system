package org.poo.models.roles;

public final class Owner implements Role {
    @Override
    public boolean canSetLimits() {
        return true;
    }

    @Override
    public boolean canPerformTransaction(double amount, String type) {
        return true;
    }

    @Override
    public String getType() {
        return "owner";
    }
}
