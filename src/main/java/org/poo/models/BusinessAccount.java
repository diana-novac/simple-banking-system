package org.poo.models;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.roles.Role;
import org.poo.models.roles.RoleFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.poo.utils.Constants.INITIAL_DEPOSIT_LIMIT;
import static org.poo.utils.Constants.INITIAL_SPENDING_LIMIT;

@Data
public final class BusinessAccount extends Account {
    private Map<String, Role> roles;
    private Map<String, Double> commerciantTotals;
    private Map<String, ArrayList<String>> commerciantUsers;
    private double spendingLimit;
    private double depositLimit;
    private Map<String, Double> spentByUser;
    private Map<String, Double> depositedByUser;

    public BusinessAccount(final CommandInput input) {
        super(input);
        roles = new LinkedHashMap<>();
        commerciantTotals = new HashMap<>();
        commerciantUsers = new LinkedHashMap<>();
        spentByUser = new HashMap<>();
        depositedByUser = new HashMap<>();

        roles.put(input.getEmail(), RoleFactory.createRole("owner"));
    }

    @Override
    public void initializeLimits(final App app) {
        spendingLimit = app.getExchangeGraph()
                .findExchangeRate("RON", getCurrency()) * INITIAL_SPENDING_LIMIT;
        depositLimit = app.getExchangeGraph()
                .findExchangeRate("RON", getCurrency()) * INITIAL_DEPOSIT_LIMIT;
    }

    @Override
    public void setRole(final String email, final Role role) {
        roles.put(email, role);
    }

    @Override
    public Role getRole(final String email) {
        return roles.get(email);
    }

    @Override
    public void changeSpendingLimit(final double amount) {
        spendingLimit = amount;
    }

    @Override
    public void changeDepositLimit(final double amount) {
        depositLimit = amount;
    }

    @Override
    public void addDepositByUser(final double amount, final String email) {
        depositedByUser.put(email, depositedByUser.getOrDefault(email, 0.0) + amount);
    }

    @Override
    public void addSpentByUser(final double amount, final String email) {
        spentByUser.put(email, spentByUser.getOrDefault(email, 0.0) + amount);
    }

    @Override
    public double getSpendingLimit() {
        return spendingLimit;
    }

    @Override
    public double getDepositLimit() {
        return depositLimit;
    }

    @Override
    public Map<String, Role> getRoles() {
        return roles;
    }

    @Override
    public Map<String, Double> getSpentByUser() {
        return spentByUser;
    }

    @Override
    public Map<String, Double> getDepositedByUser() {
        return depositedByUser;
    }

    public void addCommerciantTransaction(final String commerciant, final String email,
                                          final double amount) {
        if (getRole(email).getType().equals("employee")) {
            commerciantTotals.put(commerciant, commerciantTotals
                    .getOrDefault(commerciant, 0.0) + amount);
        }
        commerciantUsers.computeIfAbsent(commerciant, k -> new ArrayList<>()).add(email);
    }
}
