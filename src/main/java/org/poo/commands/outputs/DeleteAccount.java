package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

/**
 * Command for deleting a user's account
 */
public final class DeleteAccount implements OutputCommand {

    /**
     * Executes the delete account action
     * Validates the account's balance, logs an error if funds remain,
     * or deletes the account otherwise
     *
     * @param app     The application context
     * @param command The input containing account details
     * @return An ObjectNode containing the result of the command execution
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        User user = app.getDataContainer().getEmailMap().get(command.getEmail());
        Account account = user.getAccountMap().get(command.getAccount());

        // Check if the account has a non-zero balance
        if (account.getBalance() > 0) {
            logError(user, account, command, mapper);
            return createErrorResponse(mapper, response, command);
        }

        // Delete the account and return a success response
        deleteAccount(app, user, account);
        return createSuccessResponse(mapper, response, command);
    }

    // Deletes the account from the user and system mappings
    private void deleteAccount(final App app, final User user,
                               final Account account) {
        user.getAccounts().remove(account);
        user.getAccountMap().remove(account.getIban());
        app.getDataContainer().getAccountMap().remove(account.getIban());
    }

    // Logs an error transaction for the user and account
    private void logError(final User user, final Account account, final CommandInput command,
                          final ObjectMapper mapper) {
        ObjectNode errorTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("Account couldn't be deleted - there are funds remaining")
                .build();
        user.getTransactionHandler().addTransaction(errorTransaction);
        account.getTransactionHandler().addTransaction(errorTransaction);
    }

    // Creates an error response ObjectNode
    private ObjectNode createErrorResponse(final ObjectMapper mapper, final ObjectNode response,
                                           final CommandInput command) {
        ObjectNode output = mapper.createObjectNode();

        response.put("command", command.getCommand());
        output.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        output.put("timestamp", command.getTimestamp());
        response.putPOJO("output", output);
        response.put("timestamp", command.getTimestamp());
        return response;
    }

    // Creates a success response ObjectNode
    private ObjectNode createSuccessResponse(final ObjectMapper mapper, final ObjectNode response,
                                             final CommandInput command) {
        ObjectNode output = mapper.createObjectNode();

        response.put("command", command.getCommand());
        output.put("success", "Account deleted");
        output.put("timestamp", command.getTimestamp());
        response.putPOJO("output", output);
        response.put("timestamp", command.getTimestamp());
        return response;
    }
}
