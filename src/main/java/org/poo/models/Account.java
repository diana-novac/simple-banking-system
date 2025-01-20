package org.poo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.exceptions.AccountTypeException;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.models.roles.Role;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.poo.utils.Constants.*;

/**
 * Represents a bank account, including its properties such as IBAN, balance, currency,
 * type, cards, and transactions.
 */
@Data
public class Account {
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private double minBalance;
    private ArrayList<Card> cards;
    private String alias;
    private TransactionHandler transactionHandler;
    private double spendingAmount;
    private int numTransactions;
    private int silverTransactions;
    private HashMap<String, Integer> requiredTransactions = new HashMap<>();
    private HashMap<String, Double> discounts = new HashMap<>();

    /**
     * Constructs an Account instance based on the provided command input.
     *
     * @param input CommandInput containing details to initialize the account.
     */
    public Account(final CommandInput input) {
        iban = Utils.generateIBAN();
        balance = 0.0;
        minBalance = 0.0;
        spendingAmount = 0.0;
        numTransactions = 0;
        silverTransactions = 0;
        currency = input.getCurrency();
        type = input.getAccountType();
        cards = new ArrayList<>();

        // Initialize the transaction handler
        transactionHandler = new TransactionHandler();

        requiredTransactions.put("Food", MIN_TRANSACTIONS_FOOD_DISCOUNT);
        requiredTransactions.put("Clothes", MIN_TRANSACTIONS_CLOTHES_DISCOUNT);
        requiredTransactions.put("Tech", MIN_TRANSACTIONS_TECH_DISCOUNT);

        discounts.put("Food", FOOD_DISCOUNT_RATE);
        discounts.put("Clothes", CLOTHES_DISCOUNT_RATE);
        discounts.put("Tech", TECH_DISCOUNT_RATE);
    }

    /**
     * Converts the account details and associated cards into a JSON representation.
     *
     * @return ObjectNode containing the account's details in JSON format.
     */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode accountNode = mapper.createObjectNode();
        ArrayNode cardsArray = mapper.createArrayNode();

        accountNode.put("IBAN", iban);
        accountNode.put("balance", balance);
        accountNode.put("currency", currency);
        accountNode.put("type", type);

        for (Card card : cards) {
            cardsArray.add(card.toJson());
        }
        accountNode.set("cards", cardsArray);
        return accountNode;
    }

    /**
     * Adds a specified amount to the total spending amount of the account
     *
     * @param amount The amount to add to the spending total
     */
    public void addSpendingAmount(final double amount) {
        spendingAmount += amount;
    }

    /**
     * Adds a cashback amount to the account balance, converting the amount if necessary
     *
     * @param app    The application context
     * @param amount The cashback amount in RON
     */
    public void addCashback(final App app, final double amount) {
        double amountToAdd = amount * app.getExchangeGraph().findExchangeRate("RON", currency);
        balance += amountToAdd;
    }

    /**
     * Adds a cashback amount to the account balance and removes the discount for the category
     *
     * @param app      The application context
     * @param amount   The cashback amount in RON
     * @param category The category for which the cashback is applied
     */
    public void addCashback(final App app, final double amount, final String category) {
        double amountToAdd = amount * app.getExchangeGraph().findExchangeRate("RON", currency);
        balance += amountToAdd;
        requiredTransactions.remove(category);
        discounts.remove(category);
    }

    /**
     * Throws an exception as this method is not supported for non-savings accounts
     *
     * @throws AccountTypeException This is not a savings account
     */
    public void addInterest() {
        throw new AccountTypeException("This is not a savings account");
    }

    /**
     * Throws an exception as this method is not supported for non-savings accounts
     *
     * @param newRate The new interest rate
     * @throws AccountTypeException This is not a savings account
     */
    public void changeInterestRate(final double newRate) {
        throw new AccountTypeException("This is not a savings account");
    }

    /**
     * Throws an exception as this method is not supported for non-savings accounts
     *
     * @return The interest rate of the account
     * @throws AccountTypeException This is not a savings account
     */
    public double getInterestRate() {
        throw new AccountTypeException("This is not a savings account");
    }

    /**
     * Initializes spending and deposit limits for the account
     * (To be overridden in business accounts)
     *
     * @param app The application context
     */
    public void initializeLimits(final App app) {
    }

    /**
     * Sets a role for a user associated with this account
     * (To be overridden in business accounts)
     *
     * @param email The email of the user.
     * @param role  The role to be assigned to the user.
     */
    public void setRole(final String email, final Role role) {
    }

    /**
     * Gets the role of a user associated with this account
     * (To be overridden in business accounts)
     *
     * @param email The email of the user
     * @return The role of the user, or null if not applicable
     */
    public Role getRole(final String email) {
        return null;
    }

    public double getSpendingLimit() {
        return 0.0;
    }

    public double getDepositLimit() {
        return 0.0;
    }

    public void changeSpendingLimit(final double amount) {
    }

    public void changeDepositLimit(final double amount) {
    }

    public void addDepositByUser(final double amount, final String email) {
    }

    public void addSpentByUser(final double amount, final String email) {
    }

    public Map<String, Role> getRoles() {
        return null;
    }

    public Map<String, Double> getSpentByUser() {
        return null;
    }

    public Map<String, Double> getDepositedByUser() {
        return null;
    }
}
