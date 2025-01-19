package org.poo.commands.actions;

import org.poo.exceptions.RoleException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.roles.Role;
import org.poo.utils.CommandUtils;

public final class ChangeDepositLimit implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
            Role role = account.getRole(command.getEmail());

            if (role == null) {
                return;
            }

            if (!role.canSetLimits()) {
                throw new RoleException("You must be owner in order to change spending limit.");
            }
            account.changeDepositLimit(command.getAmount());
        } catch (RoleException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
