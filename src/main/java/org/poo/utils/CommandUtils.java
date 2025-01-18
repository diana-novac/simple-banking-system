package org.poo.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

/**
 * Utility class for handling command-related operations
 */
public final class CommandUtils {

    /**
     * Private constructor to prevent instantiation of the utility class
     */
    private CommandUtils() {
    }

    /**
     * Adds an error message to the provided output JSON array
     *
     * @param output  The ArrayNode to which the error will be added
     * @param command The CommandInput that caused the error
     * @param error   The error message
     */
    public static void addErrorToOutput(final ArrayNode output, final CommandInput command,
                                        final String error) {
        output.add(JsonUtils.createError(command, error));
    }
}
