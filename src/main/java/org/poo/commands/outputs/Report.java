package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.main.App;
import org.poo.utils.JsonUtils;

import java.util.List;

/**
 * Command for generating a detailed report for a specific account
 */
public final class Report implements OutputCommand {

    /**
     * Executes the report action
     * Retrieves the account and generates a report containing its details and transactions
     *
     * @param app     The application context
     * @param command The input containing account details and time interval
     * @return An ObjectNode containing the account report or an error message
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
        if (account == null) {
            return JsonUtils.createError(command, "Account not found");
        }

        return generateReport(account, command);
    }

    // Generates the account report including account details and filtered transactions
    private ObjectNode generateReport(final Account account, final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        ObjectNode details = mapper.createObjectNode();

        outputNode.put("command", command.getCommand());
        details.put("IBAN", account.getIban());
        details.put("balance", account.getBalance());
        details.put("currency", account.getCurrency());

        // Filter transactions by the provided time interval
        List<ObjectNode> transactions = account.getTransactionHandler()
                .filterTransactionsByInterval(command.getStartTimestamp(),
                        command.getEndTimestamp());

        ArrayNode transactionsArray = mapper.createArrayNode();
        transactions.forEach(transactionsArray::add);
        details.set("transactions", transactionsArray);

        outputNode.set("output", details);
        outputNode.put("timestamp", command.getTimestamp());

        return outputNode;
    }
}
