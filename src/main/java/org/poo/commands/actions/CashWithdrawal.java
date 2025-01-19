package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.CardNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

public final class CashWithdrawal implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User user = app.getDataContainer().getEmailMap().get(command.getEmail());
            if (user == null) {
                throw new CardNotFoundException("Card not found");
            }

            Account account = user.getAccountCardMap().get(command.getCardNumber());

            if (account == null) {
                throw new CardNotFoundException("Card not found");
            }

            Card card = app.getDataContainer().getCardMap().get(command.getCardNumber());
            if (card == null) {
                throw new CardNotFoundException("Card not found");
            }

            double amountWithdrawn = app.getExchangeGraph()
                    .findExchangeRate("RON", account.getCurrency()) * command.getAmount();
            double transactionFee = user.getAccountPlan().getTransactionFee(app, command.getAmount());

            if (account.getBalance() < (amountWithdrawn + amountWithdrawn * transactionFee)) {
                logFailedTransaction(user, account, command, "Insufficient funds");
                return;
            }

            account.setBalance(account.getBalance()
                    - (amountWithdrawn + amountWithdrawn * transactionFee));
            String description = String.format("Cash withdrawal of %.1f", command.getAmount());
            logTransaction(user, account, command, description);
            System.out.println("Acum " + user.getEmail() + " are account balance " + account.getBalance());
        } catch (CardNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    private void logFailedTransaction(final User user, final Account account,
                                final CommandInput command, final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp()).addDescription(description)
                .build();

        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }

    private void logTransaction(final User user, final Account account,
                                final CommandInput command, final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description).addAmount(command.getAmount()).build();

        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
