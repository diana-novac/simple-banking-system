package org.poo.commands.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

import java.util.ArrayList;

/**
 * Command for executing a split payment across multiple accounts
 */
public final class SplitPayment implements ActionCommand {

    /**
     * Executes the split payment action
     *
     * @param app     The application context
     * @param command The input containing details of the split payment
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        ValidationResult result = validatePayment(app, command);

        // Log a failed transaction if account validation fails
        if (result.getError() != null) {
            logFailedTransaction(result, command);
            return;
        }

        // Deducts funds from all involved accounts and logs a successful transaction
        deductFunds(app, result.getAccounts(), command, result.getAmountForEach());
        logSuccessfulTransaction(result, command);
    }

    // Validates the split payment by checking account balances and calculating deductions
    private ValidationResult validatePayment(final App app, final CommandInput command) {
        ValidationResult result = new ValidationResult();
        result.setAmountForEach(command.getAmount() / command.getAccounts().size());

        for (String iban : command.getAccounts()) {
            Account account = app.getDataContainer().getAccountMap().get(iban);

            if (account == null) {
                continue;
            }

            User user = app.getDataContainer().getUserAccountMap().get(iban);
            result.getUsers().add(user);
            result.getAccounts().add(account);

            // Convert the amount to the account's currency
            String from = command.getCurrency();
            String to = account.getCurrency();
            double rate = app.getExchangeGraph().findExchangeRate(from, to);
            double amountToDeduct = result.amountForEach * rate;

            // Check for insufficient funds
            if (account.getBalance() - amountToDeduct < 0) {
                result.setError("Account " + account.getIban()
                        + " has insufficient funds for a split payment.");
            }

            result.getInvolvedAccountsNode().add(iban);
        }

        return result;
    }

    // Deducts the calculated amount from all validated accounts
    private void deductFunds(final App app, final ArrayList<Account> accounts,
                             final CommandInput command, final double amountForEach) {
        for (Account account : accounts) {
            String from = command.getCurrency();
            String to = account.getCurrency();
            double rate = app.getExchangeGraph().findExchangeRate(from, to);
            double amountToDeduct = amountForEach * rate;

            account.setBalance(account.getBalance() - amountToDeduct);
        }
    }

    // Logs a failed transaction for all involved accounts and users
    private void logFailedTransaction(final ValidationResult result, final CommandInput command) {
        String description = String.format("Split payment of %.2f %s",
                command.getAmount(), command.getCurrency());
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description)
                .addCurrency(command.getCurrency())
                .addAmount(result.getAmountForEach())
                .addInvolvedAccounts(result.getInvolvedAccountsNode())
                .addError(result.getError()).build();

        for (int i = 0; i < result.getAccounts().size(); i++) {
            Account account = result.getAccounts().get(i);
            User user = result.getUsers().get(i);

            ObjectNode accountTransaction = transaction.deepCopy();
            account.getTransactionHandler().addTransaction(accountTransaction);

            ObjectNode userTransaction = transaction.deepCopy();
            user.getTransactionHandler().addTransaction(userTransaction);
        }
    }

    // Logs a successful transaction for all involved accounts and users
    private void logSuccessfulTransaction(final ValidationResult result,
                                          final CommandInput command) {
        String description = String.format("Split payment of %.2f %s",
                command.getAmount(), command.getCurrency());
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description)
                .addCurrency(command.getCurrency())
                .addAmount(result.getAmountForEach())
                .addInvolvedAccounts(result.getInvolvedAccountsNode()).build();

        for (int i = 0; i < result.getAccounts().size(); i++) {
            Account account = result.getAccounts().get(i);
            User user = result.getUsers().get(i);

            ObjectNode accountTransaction = transaction.deepCopy();
            account.getTransactionHandler().addTransaction(accountTransaction);

            ObjectNode userTransaction = transaction.deepCopy();
            user.getTransactionHandler().addTransaction(userTransaction);
        }
    }

    // Helper class to store validation results
    @Getter
    @Setter
    private static final class ValidationResult {
        private String error;
        private double amountForEach;
        private final ArrayList<User> users = new ArrayList<>();
        private final ArrayList<Account> accounts = new ArrayList<>();
        private final ArrayNode involvedAccountsNode = new ObjectMapper().createArrayNode();
    }
}
