package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.BusinessAccount;
import org.poo.models.User;
import org.poo.models.roles.Role;
import org.poo.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command for generating a business report for a business account
 */
public final class BusinessReport implements OutputCommand {

    /**
     * Executes the business report generation command
     * Generates and returns the report based on the specified statistics type
     *
     * @param app     The application context
     * @param command The input containing the account details and the type of report to generate
     * @return An ObjectNode containing the generated report or an error if the account is not found
     */
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
        if (account == null) {
            return JsonUtils.createError(command, "Account not found");
        }

        String statisticsType = command.getType();
        return generateReport(app, (BusinessAccount) account, command, statisticsType);
    }

    /* Generates the detailed business report for the specified account.
    The report includes general account details and specific statistics based on
    the provided type */
    private ObjectNode generateReport(final App app, final BusinessAccount account,
                                      final CommandInput command, final String statisticsType) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        ObjectNode details = mapper.createObjectNode();

        outputNode.put("command", command.getCommand());

        details.put("IBAN", account.getIban());
        details.put("balance", account.getBalance());
        details.put("currency", account.getCurrency());
        details.put("spending limit", account.getSpendingLimit());
        details.put("deposit limit", account.getDepositLimit());
        details.put("statistics type", statisticsType);

        if ("transaction".equalsIgnoreCase(statisticsType)) {
            generateTransactionStatistics(app, account, details);
        } else if ("commerciant".equalsIgnoreCase(statisticsType)) {
            generateCommerciantStatistics(app, account, details);
        }

        outputNode.set("output", details);
        outputNode.put("timestamp", command.getTimestamp());
        return outputNode;
    }

    // Generates transaction statistics for a business account
    private void generateTransactionStatistics(final App app, final BusinessAccount account,
                                               final ObjectNode details) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode managersArray = mapper.createArrayNode();
        ArrayNode employeesArray = mapper.createArrayNode();

        double totalSpent = 0.0;
        double totalDeposited = 0.0;

        for (Map.Entry<String, Role> entry : account.getRoles().entrySet()) {
            String email = entry.getKey();
            Role role = entry.getValue();

            double spent = account.getSpentByUser().getOrDefault(email, 0.0);
            double deposited = account.getDepositedByUser().getOrDefault(email, 0.0);

            User user = app.getDataContainer().getEmailMap().get(email);
            String username = user.getLastName() + " " + user.getFirstName();
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("username", username);
            userNode.put("spent", spent);
            userNode.put("deposited", deposited);

            if ("manager".equals(role.getType())) {
                managersArray.add(userNode);
            } else if ("employee".equals(role.getType())) {
                employeesArray.add(userNode);
            }

            if (!"owner".equals(role.getType())) {
                totalSpent += spent;
                totalDeposited += deposited;
            }
        }

        details.set("managers", managersArray);
        details.set("employees", employeesArray);
        details.put("total spent", totalSpent);
        details.put("total deposited", totalDeposited);
    }

    // Generates commerciant statistics for a business account
    private void generateCommerciantStatistics(final App app, final BusinessAccount account,
                                               final ObjectNode details) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode commerciantsArray = mapper.createArrayNode();

        account.getCommerciantTotals().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String commerciant = entry.getKey();
                    double totalReceived = entry.getValue();
                    List<String> users = account.getCommerciantUsers()
                            .getOrDefault(commerciant, new ArrayList<>());

                    ObjectNode commerciantNode = mapper.createObjectNode();
                    commerciantNode.put("commerciant", commerciant);
                    commerciantNode.put("total received", totalReceived);

                    ArrayNode managersArray = mapper.createArrayNode();
                    ArrayNode employeesArray = mapper.createArrayNode();

                    for (String email : users) {
                        User user = app.getDataContainer().getEmailMap().get(email);
                        if (user != null && account.getRole(email).getType().equals("employee")) {
                            String username = user.getLastName() + " " + user.getFirstName();
                            employeesArray.add(username);
                        }
                    }

                    commerciantNode.set("managers", managersArray);
                    commerciantNode.set("employees", employeesArray);

                    commerciantsArray.add(commerciantNode);
                });

        details.set("commerciants", commerciantsArray);
    }
}
