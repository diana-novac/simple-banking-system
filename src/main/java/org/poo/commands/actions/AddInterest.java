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

/**
 * Command for adding interest to a savings account
 */
public final class AddInterest implements ActionCommand {

    /**
     * Executes the add interest action
     * Applies interest to a savings account or logs an error if the account is invalid
     *
     * @param app     The application context
     * @param command The input containing account details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
            User user = app.getDataContainer().getUserAccountMap().get(command.getAccount());

            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            double interest = account.getInterestRate() * account.getBalance();
            account.addInterest();

            logTransaction(user, account, command, interest, "Interest rate income");
        } catch (AccountNotFoundException | AccountTypeException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    private void logTransaction(final User user, final Account account,
                                final CommandInput command, final double interest,
                                final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description)
                .addAmount(interest)
                .addCurrency(account.getCurrency()).build();

        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
