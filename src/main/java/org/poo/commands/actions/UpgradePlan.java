package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.plans.AccountPlan;
import org.poo.plans.AccountPlanFactory;
import org.poo.utils.TransactionBuilder;

public final class UpgradePlan implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        User user = app.getDataContainer().getUserAccountMap().get(command.getAccount());
        Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

        if (user == null || account == null) {
            return;
        }

        AccountPlan currentPlan = user.getAccountPlan();
        String desiredPlan = command.getNewPlanType();

        double upgradeFee = calculateUpgradeFee(currentPlan, desiredPlan);

        if (upgradeFee < 0) {
            return;
        }

        double amountToPay = upgradeFee * app.getExchangeGraph()
                .findExchangeRate("RON", account.getCurrency());

        if (account.getBalance() < amountToPay) {
            return;
        }

        account.setBalance(account.getBalance() - amountToPay);
        user.setAccountPlan(AccountPlanFactory.createPlan(desiredPlan));

        ObjectNode transaction = new TransactionBuilder()
                .addTimestamp(command.getTimestamp())
                .addDescription("Upgrade plan")
                .addAccountIBAN(command.getAccount())
                .addNewPlanType(desiredPlan).build();

        user.getTransactionHandler().addTransaction(transaction);
    }

    private double calculateUpgradeFee(final AccountPlan current, final String desiredPlan) {
        String currentPlan = current.getPlanName();

        if (currentPlan.equals("standard") || currentPlan.equals("student")) {
            if (desiredPlan.equals("silver")) {
                return 100.0;
            } else if (desiredPlan.equals("gold")) {
                return 350.0;
            }
        } else if (currentPlan.equals("silver")) {
            return 250.0;
        }

        return -1;
    }
}
