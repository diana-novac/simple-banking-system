package org.poo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import static org.poo.utils.Constants.*;

/**
 * Represents a bank account, including its properties such as IBAN, balance, currency,
 * type, cards, and transactions
 */
@Data
public final class Account {
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private double minBalance;
    private ArrayList<Card> cards;
    private String alias;
    private double interestRate;
    private TransactionHandler transactionHandler;
    private double spendingAmount;
    private int numTransactions;
    private HashMap<String, Integer> requiredTransactions = new HashMap<>();
    private HashMap<String, Double> discounts = new HashMap<>();

    /**
     * Constructs an Account instance based on the provided command input
     *
     * @param input CommandInput containing details to initialize the account
     */
    public Account(final CommandInput input) {
        iban = Utils.generateIBAN();
        balance = 0.0;
        minBalance = 0.0;
        spendingAmount = 0.0;
        numTransactions = 0;
        currency = input.getCurrency();
        type = input.getAccountType();
        cards = new ArrayList<>();
        interestRate = input.getInterestRate();

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
     * Converts the account details and associated cards into a JSON representation
     *
     * @return ObjectNode containing the account's details in JSON format
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

    public void addSpendingAmount(final double amount) {
        spendingAmount += amount;
    }

    public void addCashback(final App app, final double amount) {
        double amountToAdd = amount * app.getExchangeGraph().findExchangeRate("RON", currency);
        balance += amountToAdd;
    }

    public void addCashback(final double amount, final String category) {
        balance += amount;
        System.out.println("Am adaugat " + amount + " in cont");
        requiredTransactions.remove(category);
        discounts.remove(category);
        System.out.println("Am eliminat discount-ul pt categoria " + category);
    }
}
