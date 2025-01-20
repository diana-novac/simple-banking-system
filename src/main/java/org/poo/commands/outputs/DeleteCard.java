package org.poo.commands.outputs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.actions.ActionCommand;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

/**
 * Command for deleting a card associated with a user's account
 */
public final class DeleteCard implements ActionCommand {

    /**
     * Executes the delete card action
     * Removes the specified card from the user's account and logs the transaction
     *
     * @param app     The application context
     * @param command The input containing card details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getUserCardMap().get(command.getCardNumber());

        // Exit if the user associated with the card is not found
        if (user == null) {
            return;
        }

        Account account = app.getDataContainer().getAccountCardMap().get(command.getCardNumber());
        // Exit if the account associated with the card is not found
        if (account == null) {
            return;
        }

        // Exit if the card itself is not found
        Card card = findCard(account, command.getCardNumber());
        if (card == null) {
            return;
        }

        if (account.getBalance() > 0) {
            System.out.println("Vrem sa stergem cardul, dar inca avem fonduri in contul asociat");
            return;
        }
        // Delete the card and log the transaction
        deleteCard(app, user, account, card);
        logCardDeletion(user, account, command);
    }

    // Searches for the card in the account's card list by card number
    private Card findCard(final Account account, final String cardNumber) {
        for (Card card : account.getCards()) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    // Removes the card from the system
    private void deleteCard(final App app, final User user, final Account account,
                            final Card card) {
        account.getCards().remove(card);
        app.getDataContainer().getUserCardMap().remove(card.getCardNumber());
        app.getDataContainer().getCardMap().remove(card.getCardNumber());
        user.getAccountCardMap().remove(card.getCardNumber());
        app.getDataContainer().getAccountCardMap().remove(card.getCardNumber());
    }

    // Logs the card deletion as a transaction
    private void logCardDeletion(final User user, final Account account,
                                 final CommandInput command) {
        String description = "The card has been destroyed";

        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(description)
                .addCard(command.getCardNumber())
                .addCardHolder(user.getEmail())
                .addAccount(account.getIban()).build();

        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
