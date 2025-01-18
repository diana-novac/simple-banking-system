package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

/**
 * Utility class for creating JSON objects related to transactions and errors
 */
public final class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Private constructor to prevent instantiation of the utility class
     */
    private JsonUtils() {
    }

    /**
     * Creates an error object for a command execution failure
     *
     * @param command The command input that caused the error
     * @param error   The error message
     * @return An ObjectNode representing the error
     */
    public static ObjectNode createError(final CommandInput command, final String error) {
        ObjectNode errorNode = mapper.createObjectNode();
        ObjectNode output = mapper.createObjectNode();

        errorNode.put("command", command.getCommand());
        output.put("timestamp", command.getTimestamp());
        output.put("description", error);
        errorNode.putPOJO("output", output);
        errorNode.put("timestamp", command.getTimestamp());
        return errorNode;
    }

    /**
     * Creates a spending error object for unsupported operations.
     *
     * @param command The command input that caused the error
     * @param error   The error message
     * @return An ObjectNode representing the spending report error
     */
    public static ObjectNode createSpendingsError(final CommandInput command, final String error) {
        ObjectNode errorNode = mapper.createObjectNode();
        ObjectNode output = mapper.createObjectNode();

        errorNode.put("command", command.getCommand());
        output.put("error", error);
        errorNode.putPOJO("output", output);
        errorNode.put("timestamp", command.getTimestamp());
        return errorNode;
    }
}
