package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.User;

/**
 * Command for printing all users in the application
 */
public final class PrintUsers implements OutputCommand {

    /**
     * Executes the print users action
     * Retrieves all users in the application and generates a JSON response
     * containing their details
     *
     * @param app     The application context
     * @param command The input containing the command
     * @return An ObjectNode containing details of all users
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        ArrayNode usersArray = mapper.createArrayNode();

        outputNode.put("command", command.getCommand());

        // Serialize each user to JSON and add them to the users array
        for (User user : app.getUsers()) {
            usersArray.add(user.toJson());
        }

        // Add the serialized users and timestamp to the output
        outputNode.set("output", usersArray);
        outputNode.put("timestamp", command.getTimestamp());

        return outputNode;
    }
}
