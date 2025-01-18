package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.utils.TransactionBuilder;

import static org.poo.utils.Constants.MINIMUM_AGE;

public final class WithdrawSavings implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getUserAccountMap()
                .get(command.getAccount());
        Account account = app.getDataContainer().getAccountMap()
                .get(command.getAccount());

        if (user.getAge() < MINIMUM_AGE) {
            logFailedTransaction(user, account,
                    command, "You don't have the minimum age required.");
        }

        Account receiver = null;

        for (Account acc : user.getAccounts()) {
            if (acc.getType().equals("classic") && acc.getCurrency()
                    .equals(command.getCurrency())) {
                receiver = acc;
                break;
            }
        }

        if (receiver == null) {
            logFailedTransaction(user, account, command, "You do not have a classic account.");
        }
    }

    private void logFailedTransaction(final User user, final Account account,
                                      final CommandInput command, final String description) {
        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp()).addDescription(description).build();
        user.getTransactionHandler().addTransaction(transaction);
        account.getTransactionHandler().addTransaction(transaction);
    }
}
