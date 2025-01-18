package org.poo.commands.actions;

import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

/**
 * Command for setting an alias for a user's account
 */
public final class SetAlias implements ActionCommand {

    /**
     * Executes the set alias action
     * Assigns a new alias to the specified account and updates the mappings
     *
     * @param app     The application context
     * @param command The input containing account and alias details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getEmailMap().get(command.getEmail());
        Account account = user.getAccountMap().get(command.getAccount());

        // Set the new alias for the account
        account.setAlias(command.getAlias());

        // Update the system mappings to include the alias
        app.getDataContainer().getAccountMap().put(account.getAlias(), account);
        user.getAccountMap().put(account.getAlias(), account);
    }
}
