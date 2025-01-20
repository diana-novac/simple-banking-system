package org.poo.commands.actions;

import org.poo.exceptions.AccountTypeException;
import org.poo.exceptions.RoleException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.roles.Role;
import org.poo.utils.CommandUtils;

/**
 * Command for changing the spending limit of a business account.
 * This action is restricted to business accounts and requires the user
 * to be the owner
 */
public final class ChangeSpendingLimit implements ActionCommand {

    /**
     * Executes the change spending limit action
     * Validates the account type and the user's role
     *
     * @param app     The application context
     * @param command The input containing account details and the new spending limit
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

            if (!account.getType().equals("business")) {
                throw new AccountTypeException("This is not a business account");
            }

            Role role = account.getRole(command.getEmail());

            // If the role is null, the user is not associated with the account
            if (role == null) {
                return;
            }

            // Verify if the user has permission to set limits
            if (!role.canSetLimits()) {
                throw new RoleException("You must be owner in order to change spending limit.");
            }

            account.changeSpendingLimit(command.getAmount());
        } catch (RoleException | AccountTypeException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
