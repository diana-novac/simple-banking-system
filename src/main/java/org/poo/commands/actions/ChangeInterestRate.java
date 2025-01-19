package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.AccountNotFoundException;
import org.poo.exceptions.AccountTypeException;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.main.App;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

/**
 * Command for changing the interest rate of a savings account
 */
public final class ChangeInterestRate implements ActionCommand {

    /**
     * Executes the change interest rate action or logs an error
     *
     * @param app     The application context
     * @param command The input containing account details and the new interest rate
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
            User user = app.getDataContainer().getUserAccountMap().get(command.getAccount());

            // Throw an exception if the account does not exist
            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            // Update the account's interest rate
            account.changeInterestRate(command.getInterestRate());

            String description = String.format("Interest rate of the account changed to %.2f",
                    command.getInterestRate());
            ObjectNode transaction = new TransactionBuilder()
                    .addTimestamp(command.getTimestamp()).addDescription(description).build();
            account.getTransactionHandler().addTransaction(transaction);
            user.getTransactionHandler().addTransaction(transaction);
        } catch (AccountNotFoundException | AccountTypeException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
