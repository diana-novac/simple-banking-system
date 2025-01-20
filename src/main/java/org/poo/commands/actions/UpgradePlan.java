package org.poo.commands.actions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.exceptions.AccountNotFoundException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;
import org.poo.plans.AccountPlan;
import org.poo.plans.AccountPlanFactory;
import org.poo.utils.CommandUtils;
import org.poo.utils.TransactionBuilder;

import static org.poo.utils.Constants.*;

/**
 * Command for upgrading the account plan of a user
 * Handles the validation, fee calculation, and upgrade process for user account plans
 */
public final class UpgradePlan implements ActionCommand {

    /**
     * Executes the upgrade plan action.
     * Validates the action, calculates the fee, and processes the upgrade
     *
     * @param app     The application context
     * @param command The input containing account details and the desired plan
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        try {
            User user = app.getDataContainer().getUserAccountMap().get(command.getAccount());
            Account account = app.getDataContainer().getAccountMap().get(command.getAccount());

            if (user == null || account == null) {
                throw new AccountNotFoundException("Account not found");
            }

            // Get the current and desired account plan
            AccountPlan currentPlan = user.getAccountPlan();
            String desiredPlan = command.getNewPlanType();
            StringBuilder error = new StringBuilder();

            // Calculate the fee for upgrading to the desired plan
            double upgradeFee = calculateUpgradeFee(currentPlan, desiredPlan, error);
            ObjectNode transaction;

            // Handle invalid upgrade scenarios
            if (upgradeFee < 0) {
                transaction = new TransactionBuilder()
                        .addTimestamp(command.getTimestamp())
                        .addDescription(error.toString()).build();
                user.getTransactionHandler().addTransaction(transaction);
                account.getTransactionHandler().addTransaction(transaction);
                return;
            }

            double amountToPay = upgradeFee * app.getExchangeGraph()
                    .findExchangeRate("RON", account.getCurrency());

            // Check if the account has sufficient funds for the upgrade
            if (account.getBalance() < amountToPay) {
                transaction = new TransactionBuilder()
                        .addTimestamp(command.getTimestamp())
                        .addDescription("Insufficient funds").build();
                user.getTransactionHandler().addTransaction(transaction);
                account.getTransactionHandler().addTransaction(transaction);
                return;
            }

            // Deduct the upgrade fee from the account balance and set the new account plan
            account.setBalance(account.getBalance() - amountToPay);
            user.setAccountPlan(AccountPlanFactory.createPlan(desiredPlan));

            transaction = new TransactionBuilder()
                    .addTimestamp(command.getTimestamp())
                    .addDescription("Upgrade plan")
                    .addAccountIBAN(command.getAccount())
                    .addNewPlanType(desiredPlan).build();

            user.getTransactionHandler().addTransaction(transaction);
            account.getTransactionHandler().addTransaction(transaction);
        } catch (AccountNotFoundException e) {
            CommandUtils.addErrorToOutput(app.getOutput(), command, e.getMessage());
        }
    }

    // Calculates the fee required to upgrade to the desired account plan
    private double calculateUpgradeFee(final AccountPlan current, final String desiredPlan,
                                       final StringBuilder error) {
        String currentPlan = current.getPlanName();

        // Define upgrade fees based on the current and desired plans
        if (currentPlan.equals("standard") || currentPlan.equals("student")) {
            if (desiredPlan.equals("silver")) {
                return SILVER_UPGRADE_FEE;
            } else if (desiredPlan.equals("gold")) {
                return GOLD_UPGRADE_FROM_STANDARD;
            }
        } else if (currentPlan.equals("silver")) {
            if (desiredPlan.equals("gold")) {
                return GOLD_UPGRADE_FROM_SILVER;
            }
        }

        // Handle invalid downgrade scenarios
        if (currentPlan.equals("silver")
                && (desiredPlan.equals("standard") || desiredPlan.equals("student"))) {
            error.append("You cannot downgrade your plan.");
            return -1;
        }

        if (currentPlan.equals("gold") && (desiredPlan.equals("standard")
                || desiredPlan.equals("student") || desiredPlan.equals("silver"))) {
            error.append("You cannot downgrade your plan.");
            return -1;
        }

        // Handle scenarios where the user already has the desired plan
        if (currentPlan.equals(desiredPlan)) {
            error.append("The user already has the ").append(desiredPlan).append(" plan.");
        }
        return -1;
    }
}
