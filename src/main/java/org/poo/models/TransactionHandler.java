package org.poo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles transaction management: storing, adding, and filtering transactions
 */
@Getter
public class TransactionHandler {
    private final ArrayNode transactions;

    public TransactionHandler() {
        transactions = new ObjectMapper().createArrayNode();
    }

    /**
     * Adds a transaction to the transaction list
     *
     * @param transaction The transaction to add as an ObjectNode
     */
    public void addTransaction(final ObjectNode transaction) {
        transactions.add(transaction);
    }

    /**
     * Filters transactions that occurred up to a specified timestamp
     *
     * @param timestamp The maximum timestamp for filtering transactions
     * @return ArrayNode containing the filtered transactions
     */
    public ArrayNode filterTransactionsByTimestamp(final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode filteredTransactions = mapper.createArrayNode();

        transactions.forEach(transaction -> {
            if (transaction.get("timestamp").asInt() <= timestamp) {
                filteredTransactions.add(transaction);
            }
        });

        return filteredTransactions;
    }

    /**
     * Filters transactions within a specified timestamp interval
     *
     * @param startTimestamp The start of the interval
     * @param endTimestamp   The end of the interval
     * @return List of ObjectNode containing transactions within the interval
     */
    public List<ObjectNode> filterTransactionsByInterval(final int startTimestamp,
                                                         final int endTimestamp) {
        List<ObjectNode> filteredTransactions = new ArrayList<>();
        transactions.forEach(transaction -> {
            int transactionTimestamp = transaction.get("timestamp").asInt();
            if (transactionTimestamp >= startTimestamp && transactionTimestamp <= endTimestamp) {
                filteredTransactions.add((ObjectNode) transaction);
            }
        });
        return filteredTransactions;
    }

    public ArrayNode filterAndSortTransactionsByTimestamp(final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode sortedTransactions = mapper.createArrayNode();
        List<ObjectNode> filteredTransactions = new ArrayList<>();

        // Filter transactions using forEach
        transactions.forEach(transaction -> {
            if (transaction.get("timestamp").asInt() <= timestamp) {
                filteredTransactions.add((ObjectNode) transaction);
            }
        });

        // Sort the filtered transactions by timestamp
        filteredTransactions.sort(Comparator.comparingInt(t -> t.get("timestamp").asInt()));

        // Add sorted transactions to the resulting ArrayNode
        filteredTransactions.forEach(sortedTransactions::add);

        return sortedTransactions;
    }
}
