package org.poo.models.roles;

public final class Employee implements Role {
    @Override
    public boolean canSetLimits() {
        return false;
    }

    @Override
    public boolean canPerformTransaction(double amount, String type) {
        return false;
    }

    @Override
    public String getType() {
        return "employee";
    }
}
