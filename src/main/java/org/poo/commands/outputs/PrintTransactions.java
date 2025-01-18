package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.User;

/**
 * Command for printing a user's transactions
 */
public final class PrintTransactions implements OutputCommand {

    /**
     * Executes the print transactions action
     * Retrieves the user's transactions, filters them by the specified timestamp,
     * and generates a JSON response
     *
     * @param app     The application context
     * @param command The input containing user details
     * @return An ObjectNode containing the filtered transactions
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getEmailMap().get(command.getEmail());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();

        outputNode.put("command", command.getCommand());

        // Filter the user's transactions based on the given timestamp
        ArrayNode filteredTransactions = user.getTransactionHandler()
                .filterTransactionsByTimestamp(command.getTimestamp());
        outputNode.set("output", filteredTransactions);

        outputNode.put("timestamp", command.getTimestamp());
        return outputNode;
    }
}
