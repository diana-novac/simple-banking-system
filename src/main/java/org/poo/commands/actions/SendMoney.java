package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
     * Logs the transaction for both sender and receiver
     *
     * @param app     The application context
     * @param command The input containing transfer details
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User senderUser = app.getDataContainer().getEmailMap().get(command.getEmail());
            Account senderAccount = senderUser.getAccountMap().get(command.getAccount());

            Account receiverAccount = app.getDataContainer().getAccountMap()
                    .get(command.getReceiver());

            // Exit if sender or receiver account is not found
            if (senderAccount == null || receiverAccount == null) {
                throw new UserNotFoundException("User not found");
            }

            // Check if the sender is using an alias for themselves
            if (!senderAccount.getIban().equals(command.getAccount())) {
                return;
            }

            // Calculate the transferred amount in the receiver's currency
            String senderCurrency = senderAccount.getCurrency();
            String receiverCurrency = receiverAccount.getCurrency();


            double rate = app.getExchangeGraph().findExchangeRate(senderCurrency, receiverCurrency);
            double amountInRon = app.getExchangeGraph()
                    .findExchangeRate(senderCurrency, "RON") * command.getAmount();

            double transactionFee = senderUser.getAccountPlan()
                    .getTransactionFee(app, amountInRon) * command.getAmount();
            double amount = command.getAmount() * rate;

            System.out.println(senderUser.getFirstName() + " trimite " + command.getAmount() + senderAccount.getCurrency());
            System.out.println("Comisionul este de " + transactionFee + " pentru ca are rata " + senderUser.getAccountPlan().getTransactionFee(app, command.getAmount()));
            // Deduct from sender and add to receiver

            if (senderAccount.getBalance() < command.getAmount() + transactionFee) {
                logInsufficientFunds(senderUser, senderAccount, command);
                return;
            }

            senderAccount.setBalance(senderAccount.getBalance()
                    - (command.getAmount() + transactionFee));
            System.out.println("Acum " + senderUser.getEmail() + " are account balance " + senderAccount.getBalance());
            receiverAccount.setBalance(receiverAccount.getBalance() + amount);

            // Retrieve the receiver user and log the successful transaction
            User receiverUser = app.getDataContainer().getUserAccountMap()
                    .get(receiverAccount.getIban());
            logSuccessfulTransaction(senderUser, senderAccount, receiverUser,
                    receiverAccount, command, amount);
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

        ObjectNode sentTransaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription(command.getDescription())
                .addSenderIBAN(sender.getIban())
                .addReceiverIBAN(receiver.getIban())
                .addAmount(amountSent).addTransferType("sent").build();
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
}
