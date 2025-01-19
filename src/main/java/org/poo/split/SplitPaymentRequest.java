package org.poo.split;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public final class SplitPaymentRequest {
    private String type;
    private List<String> accounts;
    private double total;
    private List<Double> amounts;
    private String currency;
    private int timestamp;
    private Map<String, Boolean> responses;

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

    public void accept(User user) {
        responses.put(user.getEmail(), true);
    }

    public void reject(User user) {
        responses.put(user.getEmail(), false);
    }

    public boolean allUsersAccepted() {
        return responses.values().stream().allMatch(Boolean.TRUE::equals);
    }

    public boolean areFundsSufficient(final App app, StringBuilder error) {
        for (int i = 0; i < accounts.size(); i++) {
            User user = app.getDataContainer().getUserAccountMap().get(accounts.get(i));
            Account account = app.getDataContainer().getAccountMap().get(accounts.get(i));

            if (account == null) {
                return false;
            }
            if (amounts == null) {
                return false;
            }

            double rate = app.getExchangeGraph().findExchangeRate(currency, account.getCurrency());
            double amountToPay = amounts.get(i) * rate;

            System.out.println("Account " + account.getIban() + " trebuie sa plateasca " + amountToPay + " " + account.getCurrency());
            double amountInRon = amountToPay * app.getExchangeGraph()
                    .findExchangeRate(account.getCurrency(), "RON");
            double transactionFee = user.getAccountPlan().getTransactionFee(app, amountInRon);
            transactionFee *= amountToPay;

            System.out.println("Account " + account.getIban() + " are " + account.getBalance() +
                    " " + account.getCurrency() + " si trebuie sa plateasca " + (amountToPay + transactionFee));

            if (account.getBalance() < amountToPay + transactionFee) {
                error.append("Account ").append(account.getIban())
                        .append(" has insufficient funds for a split payment.");
                return false;
            }
        }
        return true;
    }

    public void processPayment(final App app) {
        for (int i = 0; i < accounts.size(); i++) {
            User user = app.getDataContainer().getUserAccountMap().get(accounts.get(i));
            Account account = app.getDataContainer().getAccountMap().get(accounts.get(i));
            if (account != null) {
                double rate = app.getExchangeGraph().findExchangeRate(account.getCurrency(), currency);

                if (amounts != null) {
                    double amountToPay = amounts.get(i) * rate;
                    double amountInRon = amountToPay * app.getExchangeGraph()
                            .findExchangeRate(account.getCurrency(), "RON");
                    double transactionFee = user.getAccountPlan().getTransactionFee(app, amountInRon);
                    transactionFee *= amountToPay;

                    account.setBalance(account.getBalance() - (amountToPay + transactionFee));
                }
            }
        }
    }
}