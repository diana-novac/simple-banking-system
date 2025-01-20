package org.poo.commands;

import lombok.Getter;
import org.poo.commands.actions.*;
import org.poo.commands.outputs.*;

import java.util.HashMap;

/**
 * Registry for managing all available commands in the application
 */
@Getter
public final class CommandRegistry {
    private final HashMap<String, OutputCommand> outputCommandMap = new HashMap<>();
    private final HashMap<String, ActionCommand> actionCommandMap = new HashMap<>();

    /**
     * Constructs the CommandRegistry and initializes all supported commands
     */
    public CommandRegistry() {
        initializeCommands();
    }

    /**
     * Initializes and registers all commands in the registry
     */
    private void initializeCommands() {
        // Output commands
        outputCommandMap.put("printUsers", new PrintUsers());
        outputCommandMap.put("deleteAccount", new DeleteAccount());
        outputCommandMap.put("printTransactions", new PrintTransactions());
        outputCommandMap.put("report", new Report());
        outputCommandMap.put("spendingsReport", new SpendingsReport());
        outputCommandMap.put("businessReport", new BusinessReport());

        // Action commands
        actionCommandMap.put("addAccount", new AddAccount());
        actionCommandMap.put("createCard", new CreateCard());
        actionCommandMap.put("addFunds", new AddFunds());
        actionCommandMap.put("createOneTimeCard", new CreateOneTimeCard());
        actionCommandMap.put("deleteCard", new DeleteCard());
        actionCommandMap.put("setMinimumBalance", new SetMinBalance());
        actionCommandMap.put("payOnline", new PayOnline());
        actionCommandMap.put("sendMoney", new SendMoney());
        actionCommandMap.put("splitPayment", new SplitPayment());
        actionCommandMap.put("checkCardStatus", new CheckCardStatus());
        actionCommandMap.put("changeInterestRate", new ChangeInterestRate());
        actionCommandMap.put("addInterest", new AddInterest());
        actionCommandMap.put("setAlias", new SetAlias());
        actionCommandMap.put("withdrawSavings", new WithdrawSavings());
        actionCommandMap.put("upgradePlan", new UpgradePlan());
        actionCommandMap.put("cashWithdrawal", new CashWithdrawal());
        actionCommandMap.put("acceptSplitPayment", new AcceptSplitPayment());
        actionCommandMap.put("rejectSplitPayment", new RejectSplitPayment());
        actionCommandMap.put("addNewBusinessAssociate", new AddNewBusinessAssociate());
        actionCommandMap.put("changeSpendingLimit", new ChangeSpendingLimit());
        actionCommandMap.put("changeDepositLimit", new ChangeDepositLimit());
    }
}
