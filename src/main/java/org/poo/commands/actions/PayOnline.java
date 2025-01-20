package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commerciants.Commerciant;
import org.poo.exceptions.CardNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.BusinessAccount;
import org.poo.models.Card;
import org.poo.models.User;
import org.poo.plans.AccountPlanFactory;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;
import org.poo.utils.Utils;

import static org.poo.utils.Constants.SILVER_TRANSACTION;

/**
 * Command for processing online payments using a user's card
 */
public final class PayOnline implements ActionCommand {

    /**
     * Executes the online payment action
     * Validates the card and account, calculates the payment amount,
     * and processes the transaction
     *
     * @param app     The application context
     * @param command The input containing card, account, and payment details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            // Skip if the payment amount is zero
            if (command.getAmount() == 0) {
                return;
            }

            User user = app.getDataContainer().getEmailMap().get(command.getEmail());
            Account account = app.getDataContainer().getAccountCardMap()
                    .get(command.getCardNumber());
            Card card = app.getDataContainer().getCardMap().get(command.getCardNumber());

            if (account == null || card == null) {
                throw new CardNotFoundException("Card not found");
            }

            double amountToPay = calculateAmount(app, command, account);

            // Perform additional checks for business accounts
            if (account.getType().equals("business")) {
                if (account.getRole(command.getEmail()) == null) {
                    throw new CardNotFoundException("Card not found");
                }
                if (!account.getRole(command.getEmail())
                        .canPerformTransaction(amountToPay, "spending", account)) {
                    return;
                }
                account.addSpentByUser(amountToPay, command.getEmail());
            }

            processPayment(app, user, account, card, command, amountToPay);
        } catch (CardNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Calculates the payment amount by converting the currency
    private double calculateAmount(final App app, final CommandInput command,
                                   final Account account) {
        double rate = app.getExchangeGraph().findExchangeRate(command.getCurrency(),
                account.getCurrency());
        return command.getAmount() * rate;
    }

    // Processes the payment by validating the card status, balance, and applying fees
    private void processPayment(final App app, final User user, final Account account,
                                final Card card, final CommandInput command,
                                final double amountToPay) {
        // Check if the card is active
        if (!card.getStatus().equals("active")) {
            logTransaction(user, account, command, "The card is frozen", 0, null);
            return;
        }

        // Check if the account has sufficient funds
        if (account.getBalance() < amountToPay) {
            logTransaction(user, account, command, "Insufficient funds", 0, null);
            return;
        }

        // Calculate transaction fee
        double amountInRON = amountToPay * app.getExchangeGraph()
                .findExchangeRate(account.getCurrency(), "RON");
        double transactionFee = user.getAccountPlan()
                .getTransactionFee(app, amountInRON);
        transactionFee *= amountToPay;

        account.setBalance(account.getBalance() - (amountToPay + transactionFee));

        // Try to apply cashback
        applyCashback(app, user, account, command.getCommerciant(), amountInRON);

        logTransaction(user, account, command, "Card payment", amountToPay,
                command.getCommerciant());

        // Add commerciant transaction details for business accounts
        if (account.getType().equals("business")) {
            BusinessAccount acc = (BusinessAccount) account;
            acc.addCommerciantTransaction(command.getCommerciant(), command.getEmail(),
                    amountToPay);
        }

        // Handle account plan upgrade
        handlePlanUpgrade(app, user, account, amountInRON, command);

        // Replace one-time-use card if applicable
        if (card.isOneTime()) {
            replaceOneTimeCardNumber(app, user, account, card, command);
        }
    }

    // Applies cashback based on the commerciant's strategy
    private void applyCashback(final App app, final User user, final Account account,
                               final String commerciantName, final double amountInRON) {
        Commerciant commerciant = app.getDataContainer()
                .getCommerciantMap().get(commerciantName);

        if (commerciant != null) {
            commerciant.getStrategy().applyCashback(app, user, account,
                    commerciant.getType(), amountInRON, commerciant);
        }
    }

    // Handles automatic account plan upgrades based on the user's transactions
    private void handlePlanUpgrade(final App app, final User user, final Account account,
                                   final double amountInRON, final CommandInput command) {
        if (user.getAccountPlan().getPlanName().equals("silver")) {
            if (amountInRON >= SILVER_TRANSACTION) {
                account.setSilverTransactions(account.getSilverTransactions() + 1);
            }
            if (user.getAccountPlan().automaticUpgrade(account.getSilverTransactions())) {
                user.setAccountPlan(AccountPlanFactory.createPlan("gold"));

                ObjectNode transaction = new TransactionBuilder()
                        .addTimestamp(command.getTimestamp())
                        .addDescription("Upgrade plan")
                        .addAccountIBAN(account.getIban())
                        .addNewPlanType("gold").build();

                user.getTransactionHandler().addTransaction(transaction);
                account.getTransactionHandler().addTransaction(transaction);
            }
        }
    }

    // Replaces a one-time-use card with a new card numbe
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

        updateCardMappings(app, user, card, account, oldCardNumber, newCardNumber);

        account.getTransactionHandler().addTransaction(destroyTransaction);
        account.getTransactionHandler().addTransaction(replaceTransaction);
        user.getTransactionHandler().addTransaction(destroyTransaction);
        user.getTransactionHandler().addTransaction(replaceTransaction);
    }

    // Updates card mappings in the system to reflect the new card number
    private void updateCardMappings(final App app, final User user, final Card card,
                                    final Account account, final String oldCardNumber,
                                    final String newCardNumber) {
        app.getDataContainer().getCardMap().remove(oldCardNumber);
        app.getDataContainer().getUserCardMap().remove(oldCardNumber);
        app.getDataContainer().getAccountCardMap().remove(oldCardNumber);

        card.setCardNumber(newCardNumber);
        app.getDataContainer().getCardMap().put(newCardNumber, card);
        app.getDataContainer().getUserCardMap().put(newCardNumber, user);
        app.getDataContainer().getAccountCardMap().put(newCardNumber, account);
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
