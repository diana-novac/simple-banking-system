package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.models.CardFactory;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

/**
 * Command for creating a new one-time-use card and associating it with a user's account
 */
public final class CreateOneTimeCard implements ActionCommand {

    /**
     * Executes the create one-time card action
     * Creates a new one-time-use card for the specified user and account
     *
     * @param app     The application context
     * @param command The input containing user and account details for the card
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getEmailMap().get(command.getEmail());

        // Exit if the user does not exist
        if (user == null) {
            return;
        }

        Account account = user.getAccountMap().get(command.getAccount());

        // Create a new one-time-use card and add it to the system
        Card newCard = CardFactory.createCard(true);
        addCardToSystem(newCard, user, account, app, command);
    }

    // Adds the card to the system and updates mappings and transaction history
    private void addCardToSystem(final Card card, final User user, final Account account,
                                 final App app, final CommandInput command) {
        account.getCards().add(card);
        user.getAccountCardMap().put(card.getCardNumber(), account);
        app.getDataContainer().getUserCardMap().put(card.getCardNumber(), user);
        app.getDataContainer().getCardMap().put(card.getCardNumber(), card);

        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("New card created")
                .addCard(card.getCardNumber())
                .addCardHolder(user.getEmail())
                .addAccount(account.getIban()).build();
        account.getTransactionHandler().addTransaction(transaction);
        user.getTransactionHandler().addTransaction(transaction);
    }
}
