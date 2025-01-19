package org.poo.models.roles;

public interface Role {
    boolean canSetLimits();
    boolean canPerformTransaction(double amount, String type);
    String getType();
}
