package org.poo.commands.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.UserNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.split.SplitPaymentRequest;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

/**
 * Command for accepting a split payment request
 * Handles the acceptance logic, processes payments if all users agree
 */
public final class AcceptSplitPayment implements ActionCommand {

    /**
     * Executes the accept split payment action
     *
     * @param app     The application context
     * @param command The input command containing user and payment details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User user = app.getDataContainer().getEmailMap().get(command.getEmail());
            if (user == null) {
                throw new UserNotFoundException("User not found");
            }

            String requestedType = command.getSplitPaymentType();
            SplitPaymentRequest req = user.getNextRequestOfType(requestedType);

            if (req == null) {
                return;
            }

            req.accept(user);

            if (req.allUsersAccepted()) {
                processPayment(app, req);
                removeSplitPaymentFromSystem(app, req);
            }
        } catch (UserNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Processes the payment
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

    // Removes the split payment request from the global queue
    private void removeSplitPaymentFromSystem(final App app, final SplitPaymentRequest req) {
        app.getActiveSplitPayments().remove(req);
    }

    // Builds the amounts node for the transaction
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

    // Builds the accounts node for the transaction
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

    // Builds a transaction object for the split payment
    private ObjectNode buildTransaction(final SplitPaymentRequest req, final String description,
                                        final ArrayNode amountsNode, final ArrayNode accountsNode,
                                        final String error) {
        TransactionBuilder builder = new TransactionBuilder()
                .addTimestamp(req.getTimestamp())
                .addDescription(description)
                .addSplitType(req.getType())
                .addCurrency(req.getCurrency());

        if ("custom".equals(req.getType())) {
            builder.addAmounts(amountsNode);
        } else {
            builder.addAmount(req.getAmounts().getFirst());
        }

        builder.addInvolvedAccounts(accountsNode);

        if (error != null && !error.isEmpty()) {
            builder.addError(error);
        }

        return builder.build();
    }

    // Logs the transaction for the split payment
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
