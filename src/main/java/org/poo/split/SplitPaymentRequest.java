package org.poo.split;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a request for splitting a payment among multiple accounts
 */
@Data
public final class SplitPaymentRequest {
    private final String type;
    private final List<String> accounts;
    private final double total;
    private final List<Double> amounts;
    private final String currency;
    private final int timestamp;
    private final Map<String, Boolean> responses;

    /**
     * Initializes a new SplitPaymentRequest based on the provided command input
     * and application context
     *
     * @param app     The application context
     * @param command The command input containing the split payment details
     */
    public SplitPaymentRequest(final App app, final CommandInput command) {
        type = command.getSplitPaymentType();
        accounts = command.getAccounts();
        total = command.getAmount();

        if (type.equals("custom")) {
            amounts = command.getAmountForUsers();
        } else {
            amounts = new ArrayList<>();
            double amountForEach = total / accounts.size();
            for (int i = 0; i < accounts.size(); i++) {
                amounts.add(amountForEach);
            }
        }

        currency = command.getCurrency();
        timestamp = command.getTimestamp();
        responses = new HashMap<>();

        for (String iban : accounts) {
            User user = app.getDataContainer().getUserAccountMap().get(iban);
            responses.put(user.getEmail(), null);
            user.addSplitPayment(this);
        }
    }

    /**
     * Marks a user's response as accepted for the split payment
     *
     * @param user The user who accepts the split payment
     */
    public void accept(final User user) {
        responses.put(user.getEmail(), true);
        user.getActivePaymentRequests().remove(this);
    }

    /**
     * Marks a user's response as rejected for the split payment
     *
     * @param user The user who rejects the split payment
     */
    public void reject(final User user) {
        responses.put(user.getEmail(), false);
    }

    /**
     * Checks if all users involved in the split payment have accepted it
     *
     * @return true if all users have accepted; otherwise false
     */
    public boolean allUsersAccepted() {
        return responses.values().stream().allMatch(Boolean.TRUE::equals);
    }

    /**
     * Validates whether all accounts involved in the split payment have sufficient funds
     *
     * @param app   The application context
     * @param error A StringBuilder to capture any error messages
     * @return true if all accounts have sufficient funds; otherwise false
     */
    public boolean areFundsSufficient(final App app, final StringBuilder error) {
        for (int i = 0; i < accounts.size(); i++) {
            User user = app.getDataContainer().getUserAccountMap().get(accounts.get(i));
            Account account = app.getDataContainer().getAccountMap().get(accounts.get(i));

            if (account == null || amounts == null) {
                return false;
            }

            double rate = app.getExchangeGraph().findExchangeRate(currency, account.getCurrency());
            double amountToPay = amounts.get(i) * rate;

            double amountInRon = amountToPay * app.getExchangeGraph()
                    .findExchangeRate(account.getCurrency(), "RON");
            double transactionFee = user.getAccountPlan().getTransactionFee(app, amountInRon);
            transactionFee *= amountToPay;

            if (account.getBalance() < amountToPay + transactionFee) {
                error.append("Account ").append(account.getIban())
                        .append(" has insufficient funds for a split payment.");
                return false;
            }
        }
        return true;
    }

    /**
     * Processes the split payment by deducting the amounts from each account
     *
     * @param app The application context
     */
    public void processPayment(final App app) {
        for (int i = 0; i < accounts.size(); i++) {
            Account account = app.getDataContainer().getAccountMap().get(accounts.get(i));
            if (account != null) {
                double rate = app.getExchangeGraph()
                        .findExchangeRate(currency, account.getCurrency());
                double amountToPay = amounts.get(i) * rate;
                account.setBalance(account.getBalance() - amountToPay);
            }
        }
    }
}
