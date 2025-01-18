package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commerciants.Commerciant;
import org.poo.exceptions.CardNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;
import org.poo.utils.Utils;

/**
 * Command for processing online payments using a user's card
 */
public final class PayOnline implements ActionCommand {

    /**
     * Executes the online payment action.
     * Validates the card and account, calculates the payment amount,
     * and processes the transaction
     *
     * @param app     The application context
     * @param command The input containing card, account, and payment details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            if (command.getAmount() == 0) {
                return;
            }

            User user = app.getDataContainer().getEmailMap().get(command.getEmail());
            Account account = user.getAccountCardMap().get(command.getCardNumber());

            if (account == null) {
                throw new CardNotFoundException("Card not found");
            }

            Card card = app.getDataContainer().getCardMap().get(command.getCardNumber());
            if (card == null) {
                throw new CardNotFoundException("Card not found");
            }

            double amountToPay = calculateAmount(app, command, account);
            processPayment(app, user, account, card, command, amountToPay);
        } catch (CardNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Calculates the payment amount, converting currencies if necessary
    private double calculateAmount(final App app, final CommandInput command,
                                   final Account account) {
        double rate = app.getExchangeGraph().findExchangeRate(command.getCurrency(),
                account.getCurrency());
        return command.getAmount() * rate;
    }

    // Processes the payment, handles insufficient funds, frozen cards, and one-time cards
    private void processPayment(final App app, final User user, final Account account,
                                final Card card, final CommandInput command,
                                final double amountToPay) {
        if (!card.getStatus().equals("active")) {
            logTransaction(user, account, command, "The card is frozen", 0, null);
            return;
        }

        if (account.getBalance() < amountToPay) {
            logTransaction(user, account, command, "Insufficient funds", 0, null);
            return;
        }
        System.out.println(user.getFirstName() + " are planul " + user
                .getAccountPlan().getPlanName());

        System.out.println("Va plati in " + account.getCurrency() + " suma de " + amountToPay);

        double amountInRON = amountToPay * app.getExchangeGraph()
                .findExchangeRate(account.getCurrency(), "RON");

        System.out.println("In RON va plati " + amountInRON);

        double transactionFee = user.getAccountPlan()
                .getTransactionFee(app, amountInRON);
        System.out.println("Rata comisionului e " + transactionFee);
        transactionFee *= amountToPay;

        account.setBalance(account.getBalance() - (amountToPay + transactionFee));

        System.out.println(user.getFirstName() + " plateste " + (amountToPay + transactionFee));
        System.out.println(user.getFirstName() + " are acum balanta " + account.getBalance());

        Commerciant commerciant = app.getDataContainer()
                .getCommerciantMap().get(command.getCommerciant());

        commerciant.getStrategy().applyCashback(app, user, account, commerciant.getType(),
                amountInRON);

        logTransaction(user, account, command, "Card payment", amountToPay,
                command.getCommerciant());
        if (card.isOneTime()) {
            replaceOneTimeCardNumber(app, user, account, card, command);
        }
    }

    // Replaces a one-time-use card with a new card number
    private void replaceOneTimeCardNumber(final App app, final User user,
                                          final Account account, final Card card,
                                          final CommandInput command) {
        String oldCardNumber = card.getCardNumber();
        String newCardNumber = Utils.generateCardNumber();

        ObjectNode destroyTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("The card has been destroyed")
                .addCard(oldCardNumber).addCardHolder(user.getEmail())
                .addAccount(account.getIban()).build();

        ObjectNode replaceTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("New card created").addCard(newCardNumber)
                .addCardHolder(user.getEmail())
                .addAccount(account.getIban()).build();

        updateCardMappings(app, user, card, oldCardNumber, newCardNumber);

        account.getTransactionHandler().addTransaction(destroyTransaction);
        account.getTransactionHandler().addTransaction(replaceTransaction);
        user.getTransactionHandler().addTransaction(destroyTransaction);
        user.getTransactionHandler().addTransaction(replaceTransaction);
    }

    // Updates card mappings in the system to reflect the new card number
    private void updateCardMappings(final App app, final User user, final Card card,
                                    final String oldCardNumber, final String newCardNumber) {
        app.getDataContainer().getCardMap().remove(oldCardNumber);
        app.getDataContainer().getUserCardMap().remove(oldCardNumber);

        card.setCardNumber(newCardNumber);
        app.getDataContainer().getCardMap().put(newCardNumber, card);
        app.getDataContainer().getUserCardMap().put(newCardNumber, user);
    }

    // Logs a transaction for the user and account
    private void logTransaction(final User user, final Account account,
                                final CommandInput command, final String description,
                                final double amount, final String commerciant) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp()).addDescription(description)
                .build();

        if (amount > 0) {
            transaction.put("amount", amount);
        }

        if (commerciant != null) {
            transaction.put("commerciant", commerciant);
        }

        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
