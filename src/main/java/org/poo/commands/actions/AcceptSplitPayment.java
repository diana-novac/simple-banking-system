package org.poo.commands.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.split.SplitPaymentRequest;
import org.poo.utils.TransactionBuilder;

public final class AcceptSplitPayment implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getEmailMap().get(command.getEmail());
        if (user == null) {
            return;
        }

        String requestedType = command.getSplitPaymentType();
        SplitPaymentRequest req = user.getNextRequestOfType(requestedType);

        if (req == null) {
            return;
        }

        req.accept(user);

        if (req.allUsersAccepted()) {
            processPayment(app, req);
        }
    }

    private void processPayment(final App app, final SplitPaymentRequest req) {
        ArrayNode amountsNode = buildAmountsNode(req);
        ArrayNode accountsNode = buildAccountsNode(req);

        String description = String.format("Split payment of %.2f %s",
                req.getTotal(), req.getCurrency());
        StringBuilder error = new StringBuilder();

        if (req.areFundsSufficient(app, error)) {
            req.processPayment(app);
            ObjectNode transaction = buildTransaction(req, description, amountsNode,
                    accountsNode, null);
            logTransaction(app, req, transaction);
        } else {
            ObjectNode transaction = buildTransaction(req, description, amountsNode,
                    accountsNode, error.toString());
            logTransaction(app, req, transaction);
        }
    }

    private ArrayNode buildAmountsNode(final SplitPaymentRequest req) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode amountsNode = mapper.createArrayNode();

        if (req.getAmounts() != null) {
            for (Double amount : req.getAmounts()) {
                amountsNode.add(amount);
            }
        }
        return amountsNode;
    }

    private ArrayNode buildAccountsNode(final SplitPaymentRequest req) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode accountsNode = mapper.createArrayNode();

        if (req.getAccounts() != null) {
            for (String iban : req.getAccounts()) {
                accountsNode.add(iban);
            }
        }
        return accountsNode;
    }

    private ObjectNode buildTransaction(final SplitPaymentRequest req, final String description,
                                        final ArrayNode amountsNode, final ArrayNode accountsNode,
                                        final String error) {
        TransactionBuilder builder = new TransactionBuilder()
                .addTimestamp(req.getTimestamp())
                .addDescription(description)
                .addSplitType(req.getType())
                .addCurrency(req.getCurrency())
                .addInvolvedAccounts(accountsNode);

        if ("custom".equals(req.getType())) {
            builder.addAmounts(amountsNode);
        } else {
            builder.addAmount(req.getAmounts().getFirst());
        }

        if (error != null && !error.isEmpty()) {
            builder.addError(error);
        }

        return builder.build();
    }

    private void logTransaction(final App app, final SplitPaymentRequest req,
                                                  final ObjectNode transaction) {
        for (String iban : req.getAccounts()) {
            User user = app.getDataContainer().getUserAccountMap().get(iban);
            Account account = app.getDataContainer().getAccountMap().get(iban);

            if (user != null) {
                user.getTransactionHandler().addTransaction(transaction);
            }
            if (account != null) {
                account.getTransactionHandler().addTransaction(transaction);
            }
        }
    }
}
