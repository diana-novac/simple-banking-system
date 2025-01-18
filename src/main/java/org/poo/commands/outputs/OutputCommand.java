package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;

/**
 * Represents a command that generates output within the application
 */
public interface OutputCommand {

    /**
     * Executes the output command
     * Processes the input and application context to generate a result as an ObjectNode
     *
     * @param app     The application context
     * @param command The input parameters for the command
     * @return An ObjectNode containing the result of the command execution
     */
    ObjectNode execute(App app, CommandInput command);
}
