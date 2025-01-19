package org.poo.commands.actions;

import org.poo.exceptions.AccountNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.models.roles.RoleFactory;
import org.poo.utils.CommandUtils;

public final class AddNewBusinessAssociate implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
            User user = app.getDataContainer().getEmailMap().get(command.getEmail());

            if (account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            account.setRole(command.getEmail(), RoleFactory.createRole(command.getRole()));
            user.getAccounts().add(account);
        } catch (AccountNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }
}
