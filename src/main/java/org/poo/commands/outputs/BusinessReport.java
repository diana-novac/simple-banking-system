package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.models.roles.Role;
import org.poo.utils.JsonUtils;

import java.util.Map;

public final class BusinessReport implements OutputCommand {
    @Override
    public ObjectNode execute(final App app, final CommandInput command) {
        Account account = app.getDataContainer().getAccountMap().get(command.getAccount());
        if (account == null) {
            return JsonUtils.createError(command, "Account not found");
        }

        return generateTransactionReport(app, account, command);
    }

    private ObjectNode generateTransactionReport(final App app, final Account account,
                                                 final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        ObjectNode details = mapper.createObjectNode();

        outputNode.put("command", command.getCommand());

        // Detalii generale despre cont
        details.put("IBAN", account.getIban());
        details.put("balance", account.getBalance());
        details.put("currency", account.getCurrency());
        details.put("spending limit", account.getSpendingLimit());
        details.put("deposit limit", account.getDepositLimit());
        details.put("statistics type", "transaction");

        // Generăm statistici pentru utilizatori
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

            if (role.getType().equals("manager")) {
                managersArray.add(userNode);
            } else if (role.getType().equals("employee")) {
                employeesArray.add(userNode);
            }

            if (!role.getType().equals("owner")) {
                totalSpent += spent;
                totalDeposited += deposited;
            }
        }

        details.set("managers", managersArray);
        details.set("employees", employeesArray);
        details.put("total spent", totalSpent);
        details.put("total deposited", totalDeposited);

        outputNode.set("output", details);
        outputNode.put("timestamp", command.getTimestamp());
        return outputNode;
    }
}
