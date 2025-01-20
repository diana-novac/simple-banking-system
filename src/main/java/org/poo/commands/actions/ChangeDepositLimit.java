package org.poo.commands.actions;

import org.poo.exceptions.RoleException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.roles.Role;
import org.poo.utils.CommandUtils;

/**
 * Command for changing the deposit limit of a business account
 * This action is restricted to users with the owner role
 */
public final class ChangeDepositLimit implements ActionCommand {

    /**
     * Executes the change deposit limit action
     * Validates the user's role before allowing changes to the deposit limit
     *
     * @param app     The application context
     * @param command The input containing account details and the new deposit limit
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
            Role role = account.getRole(command.getEmail());

            // If the role is null, the user is not associated with the account
            if (role == null) {
                return;
            }

            // Verify if the user has permission to set limits
            if (!role.canSetLimits()) {
                throw new RoleException("You must be owner in order to change deposit limit.");
            }

            account.changeDepositLimit(command.getAmount());
        } catch (RoleException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
