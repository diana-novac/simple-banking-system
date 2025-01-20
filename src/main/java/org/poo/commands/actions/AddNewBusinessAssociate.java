package org.poo.commands.actions;

import org.poo.exceptions.AccountNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.roles.RoleFactory;
import org.poo.utils.CommandUtils;

/**
 * Command for adding a new business associate to a business account
 * Associates a role with the specified user email
 */
public final class AddNewBusinessAssociate implements ActionCommand {

    /**
     * Executes the action to add a new business associate to a specified account
     * Validates the account and ensures that the user does not already have an assigned role
     *
     * @param app     The application context
     * @param command The input containing account details and associate information
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            // Check if the user already has a role in the account
            if (account.getRoles().containsKey(command.getEmail())) {
                return;
            }

            // Assign the role to the email using the role factory
            account.setRole(command.getEmail(), RoleFactory.createRole(command.getRole()));
        } catch (AccountNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
