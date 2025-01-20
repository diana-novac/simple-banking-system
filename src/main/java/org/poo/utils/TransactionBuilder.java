package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A builder class for creating JSON objects
 * representing transactions step by step.
 *
 */
public final class TransactionBuilder {
    private final ObjectNode transaction;

    /**
     * Constructs a new instance of the TransactionBuilder
     */
    public TransactionBuilder() {
        ObjectMapper mapper = new ObjectMapper();
        transaction = mapper.createObjectNode();
    }

    /**
     * Adds a timestamp to the transaction
     *
     * @param timestamp The timestamp of the transaction
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addTimestamp(final int timestamp) {
        transaction.put("timestamp", timestamp);
        return this;
    }

    /**
     * Adds a description to the transaction
     *
     * @param description A text description
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addDescription(final String description) {
        transaction.put("description", description);
        return this;
    }

    /**
     * Adds a card number involved in the transaction
     *
     * @param cardNumber The card number
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addCard(final String cardNumber) {
        transaction.put("card", cardNumber);
        return this;
    }

    /**
     * Adds the cardholder's email to the transaction
     *
     * @param cardHolder The cardholder's email address
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addCardHolder(final String cardHolder) {
        transaction.put("cardHolder", cardHolder);
        return this;
    }

    /**
     * Adds the account IBAN associated with the transaction
     *
     * @param account The account IBAN
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addAccount(final String account) {
        transaction.put("account", account);
        return this;
    }

    /**
     * Adds the sender's IBAN to the transaction
     *
     * @param senderIBAN The sender's IBAN
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addSenderIBAN(final String senderIBAN) {
        transaction.put("senderIBAN", senderIBAN);
        return this;
    }

    /**
     * Adds the receiver's IBAN to the transaction
     *
     * @param receiverIBAN The receiver's IBAN
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addReceiverIBAN(final String receiverIBAN) {
        transaction.put("receiverIBAN", receiverIBAN);
        return this;
    }

    /**
     * Adds the transaction amount as a numeric value
     *
     * @param amount The transaction amount as a double
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addAmount(final double amount) {
        transaction.put("amount", amount);
        return this;
    }

    /**
     * Adds the transaction amount as a string
     *
     * @param amount The transaction amount as a string
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addAmount(final String amount) {
        transaction.put("amount", amount);
        return this;
    }

    /**
     * Adds the type of transfer for the transaction
     *
     * @param type The type of transfer
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addTransferType(final String type) {
        transaction.put("transferType", type);
        return this;
    }

    /**
     * Adds the currency of the transaction
     *
     * @param currency The currency string
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addCurrency(final String currency) {
        transaction.put("currency", currency);
        return this;
    }

    /**
     * Adds a list of accounts involved in the transaction
     *
     * @param involvedAccounts A JSON array containing the involved accounts
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addInvolvedAccounts(final ArrayNode involvedAccounts) {
        transaction.set("involvedAccounts", involvedAccounts);
        return this;
    }

    /**
     * Adds an error message to the transaction
     *
     * @param error An error message related to the transaction
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addError(final String error) {
        transaction.put("error", error);
        return this;
    }

    /**
     * Adds the new plan type to the transaction
     *
     * @param newPlanType The type of the new plan being applied
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addNewPlanType(final String newPlanType) {
        transaction.put("newPlanType", newPlanType);
        return this;
    }

    /**
     * Adds an IBAN associated with the account involved in the transaction
     *
     * @param iban The account IBAN
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addAccountIBAN(final String iban) {
        transaction.put("accountIBAN", iban);
        return this;
    }

    /**
     * Adds the type of split payment to the transaction
     *
     * @param type The type of the split payment
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addSplitType(final String type) {
        transaction.put("splitPaymentType", type);
        return this;
    }

    /**
     * Adds the list of amounts for users in the split payment
     *
     * @param amounts A JSON array containing the amounts for users
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addAmounts(final ArrayNode amounts) {
        transaction.set("amountForUsers", amounts);
        return this;
    }

    /**
     * Adds the IBAN of a classic account involved in the transaction
     *
     * @param receiverIBAN The IBAN of the classic account
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addClassicIBAN(final String receiverIBAN) {
        transaction.put("classicAccountIBAN", receiverIBAN);
        return this;
    }

    /**
     * Adds the IBAN of a savings account involved in the transaction
     *
     * @param savingsIBAN The IBAN of the savings account
     * @return The current instance of the builder for method chaining
     */
    public TransactionBuilder addSavingsIBAN(final String savingsIBAN) {
        transaction.put("savingsAccountIBAN", savingsIBAN);
        return this;
    }
    /**
     * Finalizes the transaction
     *
     * @return The complete transaction as an ObjectNode
     */
    public ObjectNode build() {
        return transaction;
    }
}
