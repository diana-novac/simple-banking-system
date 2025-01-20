package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.AccountNotFoundException;
import org.poo.exceptions.AccountTypeException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

import static org.poo.utils.Constants.MINIMUM_AGE;

/**
 * Command for withdrawing funds from a savings account
 * The withdrawn amount is transferred to a user's classic account in the specified currency
 */
public final class WithdrawSavings implements ActionCommand {
    /**
     * Executes the savings withdrawal action
     * Validates the withdrawal and transfers the amount to a classic account
     *
     * @param app     The application context
     * @param command The input containing account details and the amount to withdraw
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User user = app.getDataContainer().getUserAccountMap()
                    .get(command.getAccount());
            Account account = app.getDataContainer().getAccountMap()
                    .get(command.getAccount());

            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            // Validate the minimum age requirement
            if (user.getAge() < MINIMUM_AGE) {
                logFailedTransaction(user, account,
                        command, "You don't have the minimum age required.");
                return;
            }

            Account receiver = null;
            // Find a classic account with the specified currency
            for (Account acc : user.getAccounts()) {
                if (acc.getType().equals("classic") && acc.getCurrency()
                        .equals(command.getCurrency())) {
                    receiver = acc;
                    break;
                }
            }

            if (receiver == null) {
                logFailedTransaction(user, account, command, "You do not have a classic account.");
                return;
            }

            double amountToWithdraw = command.getAmount() * app.getExchangeGraph()
                    .findExchangeRate(command.getCurrency(), account.getCurrency());

            if (account.getBalance() < amountToWithdraw) {
                logFailedTransaction(user, account, command, "Insufficient funds");
                return;
            }

            account.setBalance(account.getBalance() - amountToWithdraw);
            receiver.setBalance(receiver.getBalance() + command.getAmount());

            logTransaction(user, account, receiver, command);
        } catch (AccountNotFoundException | AccountTypeException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Logs a failed transaction in the user's transaction history
    private void logFailedTransaction(final User user, final Account account,
                                      final CommandInput command, final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp()).addDescription(description).build();
        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }

    // Logs a successful savings withdrawal transaction
    private void logTransaction(final User user, final Account savingsAcc,
                                final Account receiverAcc, final CommandInput command) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp()).addDescription("Savings withdrawal")
                .addSavingsIBAN(savingsAcc.getIban()).addClassicIBAN(receiverAcc.getIban())
                .addAmount(command.getAmount()).build();

        user.getTransactionHandler().addTransaction(transaction);
        user.getTransactionHandler().addTransaction(transaction);
        savingsAcc.getTransactionHandler().addTransaction(transaction);
        receiverAcc.getTransactionHandler().addTransaction(transaction);
    }
}
