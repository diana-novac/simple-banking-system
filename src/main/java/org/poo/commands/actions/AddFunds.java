package org.poo.commands.actions;

import org.poo.exceptions.AccountNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.main.App;
import org.poo.utils.CommandUtils;

/**
 * Command for adding funds to a specified account within the application
 */
public final class AddFunds implements ActionCommand {

    /**
     * Executes the add funds action
     * Increases the balance of the specified account or logs an error
     * in case of failure
     *
     * @param app     The application context
     * @param command The input containing account details and the amount to add
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

            // Throw an exception if the account does not exist
            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            if (account.getType().equals("business")) {
                // Ensure the user has a valid role in the account
                if (account.getRole(command.getEmail()) == null) {
                    return;
                }

                // Check if the user is not authorized to perform this transaction
                if (!account.getRole(command.getEmail())
                        .canPerformTransaction(command.getAmount(), "deposit", account)) {
                    return;
                }

                // Log the deposit made by this user
                account.addDepositByUser(command.getAmount(), command.getEmail());
            }

            // Add the specified amount to the account balance
            account.setBalance(account.getBalance() + command.getAmount());
        } catch (AccountNotFoundException e) {
            // Log an error to the application's output if the account is not found
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
