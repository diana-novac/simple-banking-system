package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.commands.CommandRegistry;
import org.poo.commerciants.Commerciant;
import org.poo.data.CommerciantInitializer;
import org.poo.data.DataContainer;
import org.poo.data.UserInitializer;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.models.ExchangeRateGraph;
import org.poo.models.User;

import java.util.ArrayList;

/**
 * The App class serves as the main entry point for processing the commands.
 * It initializes the required components and executes the flow of commands
 * provided as input.
 */
@Data
public final class App {
    private ArrayList<User> users;
    private ArrayList<Commerciant> commerciants;
    private ExchangeRateGraph exchangeGraph;
    private DataContainer dataContainer = new DataContainer();
    private CommandInput[] commands;
    private CommandRegistry commandRegistry;
    private ArrayNode output;

    /**
     * Constructs an App instance with the provided input data
     *
     * @param input ObjectInput containing the initial configuration
     */
    public App(final ObjectInput input) {
        ObjectMapper mapper = new ObjectMapper();
        output = mapper.createArrayNode();

        // Initialize the exchange rate graph
        exchangeGraph = new ExchangeRateGraph(input.getExchangeRates());

        // Load users
        users = new UserInitializer().loadUsers(input.getUsers(), dataContainer);
        commerciants = new CommerciantInitializer()
                .loadCommerciants(input.getCommerciants(), dataContainer);

        // Initialize the command registry and load commands
        commandRegistry = new CommandRegistry();
        commands = input.getCommands();
    }

    /**
     * Executes the flow of commands by invoking the appropriate handlers
     * for each command in the sequence
     */
    public void flow() {
        for (CommandInput command : commands) {
            if (commandRegistry.getOutputCommandMap().containsKey(command.getCommand())) {
                output.add(commandRegistry.getOutputCommandMap()
                        .get(command.getCommand()).execute(this, command));
            } else if (commandRegistry.getActionCommandMap().containsKey(command.getCommand())) {
                commandRegistry.getActionCommandMap()
                        .get(command.getCommand()).execute(this, command);
            }
        }
    }
}
