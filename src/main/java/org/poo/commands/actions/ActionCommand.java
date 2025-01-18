package org.poo.commands.actions;

import org.poo.fileio.CommandInput;
import org.poo.main.App;

/**
 * Represents a command that performs an action within the application
 */
public interface ActionCommand {

    /**
     * Executes the action represented by the command
     *
     * @param app     The main application context
     * @param command The input parameters for the command
     */
    void execute(App app, CommandInput command);
}
