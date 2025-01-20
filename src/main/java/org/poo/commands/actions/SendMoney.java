package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commerciants.Commerciant;
import org.poo.exceptions.UserNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

/**
 * Command for transferring money between accounts
 */
public final class SendMoney implements ActionCommand {

    /**
     * Executes the send money action
     * Transfers money between accounts after validating the sender's account and balance
     *
     * @param app     The application context
     * @param command The input containing transfer details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User senderUser = app.getDataContainer().getEmailMap().get(command.getEmail());
            Account senderAccount = senderUser.getAccountMap().get(command.getAccount());
            boolean onlinePayment = false;
            Account receiverAccount = app.getDataContainer().getAccountMap()
                    .get(command.getReceiver());

            // Check if sender account exists
            if (senderAccount == null) {
                throw new UserNotFoundException("User not found");
            }

            // Check if it's an online payment
            if (receiverAccount == null) {
                if (app.getDataContainer().getCommerciantAccountMap()
                        .get(command.getReceiver()) == null) {
                    throw new UserNotFoundException("User not found");
                }
                onlinePayment = true;
            }

            // Check if the sender is using an alias for themselves
            if (!senderAccount.getIban().equals(command.getAccount())) {
                return;
            }

            // Calculate the transferred amount in the receiver's currency
            String senderCurrency = senderAccount.getCurrency();
            String receiverCurrency;
            double rate;
            double amountReceived = 0.0;

            if (!onlinePayment) {
                receiverCurrency = receiverAccount.getCurrency();
                rate = app.getExchangeGraph().findExchangeRate(senderCurrency, receiverCurrency);
                amountReceived = command.getAmount() * rate;
            }

            // Calculate the transaction fee in RON
            double amountInRon = app.getExchangeGraph()
                    .findExchangeRate(senderCurrency, "RON") * command.getAmount();
            double transactionFee = senderUser.getAccountPlan()
                    .getTransactionFee(app, amountInRon) * command.getAmount();

            // Check if the sender has sufficient funds
            if (senderAccount.getBalance() < command.getAmount() + transactionFee) {
                logInsufficientFunds(senderUser, senderAccount, command);
                return;
            }

            // Deduct the amount and transaction fee from sender's account
            senderAccount.setBalance(senderAccount.getBalance()
                    - (command.getAmount() + transactionFee));

            if (!onlinePayment) {
                // Update the receiver's account balance it is not an online payment
                receiverAccount.setBalance(receiverAccount.getBalance() + amountReceived);
                User receiverUser = app.getDataContainer().getUserAccountMap()
                        .get(receiverAccount.getIban());
                logSuccessfulTransaction(senderUser, senderAccount, receiverUser,
                        receiverAccount, command, amountReceived);
                return;
            }

            // Log the transaction for online payments
            logTransaction(senderUser, senderAccount, command);

            // Try to apply cashback
            Commerciant commerciant = app.getDataContainer().getCommerciantAccountMap()
                    .get(command.getReceiver());
            commerciant.getStrategy().applyCashback(app, senderUser, senderAccount,
                    commerciant.getType(), command.getAmount(), commerciant);
        } catch (UserNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Logs a transaction for insufficient funds in the sender's account
    private void logInsufficientFunds(final User user, final Account account,
                                      final CommandInput command) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("Insufficient funds").build();
        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }

    // Logs a successful transaction for both the sender and receiver
    private void logSuccessfulTransaction(final User senderUser, final Account sender,
                                          final User receiverUser, final Account receiver,
                                          final CommandInput command, final double amount) {
        String amountSent = command.getAmount() + " " + sender.getCurrency();
        String amountReceived = amount + " " + receiver.getCurrency();

        // Create and log transaction details for the sender
        ObjectNode sentTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(command.getDescription())
                .addSenderIBAN(sender.getIban())
                .addReceiverIBAN(receiver.getIban())
                .addAmount(amountSent).addTransferType("sent").build();

        // Create and log transaction details for the receiver
        ObjectNode receivedTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(command.getDescription())
                .addSenderIBAN(sender.getIban())
                .addReceiverIBAN(receiver.getIban())
                .addAmount(amountReceived).addTransferType("received").build();

        senderUser.getTransactionHandler().addTransaction(sentTransaction);
        sender.getTransactionHandler().addTransaction(sentTransaction);
        receiverUser.getTransactionHandler().addTransaction(receivedTransaction);
        receiver.getTransactionHandler().addTransaction(receivedTransaction);
    }

    // Logs a transaction for online payments
    private void logTransaction(final User user, final Account account,
                                final CommandInput command) {
        String amount = String.format("%.1f %s", command.getAmount(), account.getCurrency());
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(command.getDescription())
                .addSenderIBAN(account.getIban())
                .addReceiverIBAN(command.getReceiver())
                .addAmount(amount).addTransferType("sent").build();
        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
