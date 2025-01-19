package org.poo.models.roles;

public final class Manager implements Role {
    @Override
    public boolean canSetLimits() {
        return false;
    }

    @Override
    public boolean canPerformTransaction(double amount, String type) {
        return true;
    }

    @Override
    public String getType() {
        return "manager";
    }
}
