package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.utils.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Command for generating a spending report for a specified account
 * The report includes filtered transactions and a summary of amounts grouped by commerciants
 */
public final class SpendingsReport implements OutputCommand {

    /**
     * Executes the spending report action
     * Validates the account type and generates a spending report containing filtered transactions
     * and a commerciant summary
     *
     * @param app     The application context
     * @param command The input containing account details and time interval
     * @return An ObjectNode containing the spending report or an error message
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
        if (account == null) {
            return JsonUtils.createError(command, "Account not found");
        }

        if (account.getType().equals("savings")) {
            return JsonUtils.createSpendingsError(command,
                    "This kind of report is not supported for a saving account");
        }

        return generateSpendingsReport(account, command);
    }

    // Generates the spending report
    private ObjectNode generateSpendingsReport(final Account account, final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        ObjectNode details = mapper.createObjectNode();

        // Add account details to the report
        outputNode.put("command", command.getCommand());
        details.put("IBAN", account.getIban());
        details.put("balance", account.getBalance());
        details.put("currency", account.getCurrency());

        // Add filtered transactions and commerciant summary to the report
        ArrayNode filteredTransactions = generateFilteredTransactions(account, command);
        ArrayNode commerciantSummary = generateCommerciantSummary(account, command);

        details.set("transactions", filteredTransactions);
        details.set("commerciants", commerciantSummary);
        outputNode.set("output", details);
        outputNode.put("timestamp", command.getTimestamp());

        return outputNode;
    }

    // Filters transactions to include only card payments
    private ArrayNode generateFilteredTransactions(final Account account,
                                                   final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode filteredTransactions = mapper.createArrayNode();

        List<ObjectNode> transactions = account.getTransactionHandler()
                .filterTransactionsByInterval(command.getStartTimestamp(),
                        command.getEndTimestamp());

        for (ObjectNode transaction : transactions) {
            if ("Card payment".equals(transaction.get("description").asText())) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    // Generates a summary of spending grouped by commerciants
    private ArrayNode generateCommerciantSummary(final Account account,
                                                 final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode commerciantSummary = mapper.createArrayNode();

        List<ObjectNode> transactions = account.getTransactionHandler()
                .filterTransactionsByInterval(command.getStartTimestamp(),
                        command.getEndTimestamp());

        Map<String, Double> commerciantTotals = new TreeMap<>();
        for (ObjectNode transaction : transactions) {
            JsonNode commerciantJsonNode = transaction.get("commerciant");
            if (commerciantJsonNode != null) {
                String commerciant = commerciantJsonNode.asText();
                double amount = transaction.get("amount").asDouble();
                commerciantTotals.merge(commerciant, amount, Double::sum);
            }
        }

        commerciantTotals.forEach((commerciant, total) -> {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant);
            commerciantNode.put("total", total);
            commerciantSummary.add(commerciantNode);
        });

        return commerciantSummary;
    }
}
