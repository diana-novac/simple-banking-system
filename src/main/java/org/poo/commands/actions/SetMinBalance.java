package org.poo.commands.actions;

import org.poo.exceptions.AccountNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.main.App;
import org.poo.utils.CommandUtils;

/**
 * Command for setting the minimum balance of a user's account
 */
public final class SetMinBalance implements ActionCommand {

    /**
     * Executes the set minimum balance action.
     * Updates the minimum balance of the specified account or logs an error
     *
     * @param app     The application context
     * @param command The input containing account details and the new minimum balance
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

            // Throw an exception if the account does not exist
            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            // Set the new minimum balance for the account
            account.setMinBalance(command.getMinBalance());
        } catch (AccountNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
