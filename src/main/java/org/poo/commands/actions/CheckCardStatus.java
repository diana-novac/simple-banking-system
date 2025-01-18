package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.CardNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.main.App;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

/**
 * Command for checking the status of a card and freezing it if the associated
 * account balance reaches the minimum allowed balance
 */
public final class CheckCardStatus implements ActionCommand {

    /**
     * Executes the check card status action
     * Validates the card and its associated account, freezes all cards if necessary,
     * and logs transactions when the account balance reaches the minimum
     *
     * @param app     The application context
     * @param command The input containing card details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User user = app.getDataContainer().getUserCardMap().get(command.getCardNumber());

            // Throw an exception if the card is not found
            if (user == null) {
                throw new CardNotFoundException("Card not found");
            }

            Account account = user.getAccountCardMap().get(command.getCardNumber());

            if (account == null) {
                return;
            }

            // Freeze cards if the balance is less than or equal to the minimum balance
            if (account.getBalance() <= account.getMinBalance()) {
                freezeAllCards(account);

                // Log a transaction if the balance equals the minimum balance
                if (account.getBalance() == account.getMinBalance()) {
                    String description = "You have reached the minimum amount of funds, "
                            + "the card will be frozen";
                    logTransaction(user, account, command, description);
                }
            }
        } catch (CardNotFoundException e) {
            // Log an error to the application's output
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    private void freezeAllCards(final Account account) {
        for (Card card : account.getCards()) {
            card.setStatus("frozen");
        }
    }

    private void logTransaction(final User user, final Account account,
                                final CommandInput command, final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description).build();
        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
