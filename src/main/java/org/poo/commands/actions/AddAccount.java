package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.main.App;
import org.poo.models.AccountFactory;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

/**
 * Command for adding a new account to a user within the application
 */
public final class AddAccount implements ActionCommand {

    /**
     * Creates a new account based on the command input, associates it with the user
     *
     * @param app     The main application context
     * @param command The input containing details for creating the account
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getEmailMap().get(command.getEmail());

        // Create a new account using the AccountFactory and update the mappings
        Account newAccount = AccountFactory.createAccount(command);

        user.getAccounts().add(newAccount);
        user.getAccountMap().put(newAccount.getIban(), newAccount);

        app.getDataContainer().getAccountMap().put(newAccount.getIban(), newAccount);
        app.getDataContainer().getUserAccountMap().put(newAccount.getIban(), user);

        // Record the creation of a new account in the transaction history
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("New account created").build();
        user.getTransactionHandler().addTransaction(transaction);
        newAccount.getTransactionHandler().addTransaction(transaction);
    }
}
