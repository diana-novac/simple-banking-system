package org.poo.models;

import org.poo.fileio.ExchangeInput;
import org.poo.utils.Pair;

import java.util.*;

/**
 * Represents a graph structure to manage and retrieve exchange rates between currencies
 */
public final class ExchangeRateGraph {
    private final Map<String, Map<String, Double>> graph = new HashMap<>();

    /**
     * Constructs an ExchangeRateGraph and initializes it with the provided exchange rates
     *
     * @param inputs Array of ExchangeInput objects
     */
    public ExchangeRateGraph(final ExchangeInput[] inputs) {
        for (ExchangeInput input : inputs) {
            // Ensure both currencies are present in the graph
            graph.putIfAbsent(input.getFrom(), new HashMap<>());
            graph.putIfAbsent(input.getTo(), new HashMap<>());

            // Add the exchange rate and its inverse to the graph
            graph.get(input.getFrom()).put(input.getTo(), input.getRate());
            graph.get(input.getTo()).put(input.getFrom(), 1.0 / input.getRate());
        }
    }

    /**
     * Finds the exchange rate between two currencies using breadth-first search
     *
     * @param from Source currency
     * @param to   Target currency
     * @return The exchange rate from the source currency to the target currency
     * @throws IllegalArgumentException if the currencies are not supported or no path exists
     */
    public double findExchangeRate(final String from, final String to) {
        if (!graph.containsKey(from) || !graph.containsKey(to)) {
            throw new IllegalArgumentException("Currencies not supported");
        }

        // Start BFS with the source currency and a rate of 1.0
        Set<String> visited = new HashSet<>();
        Queue<Pair<String, Double>> queue = new LinkedList<>();
        queue.add(new Pair<>(from, 1.0));

        while (!queue.isEmpty()) {
            Pair<String, Double> current = queue.poll();
            String currentCurrency = current.getKey();
            double currentRate = current.getValue();

            // Return the rate if the target currency is reached
            if (currentCurrency.equals(to)) {
                return currentRate;
            }
            visited.add(currentCurrency);

            // Traverse neighbors and calculate cumulative exchange rates
            for (Map.Entry<String, Double> node : graph.get(currentCurrency).entrySet()) {
                if (!visited.contains(node.getKey())) {
                    queue.add(new Pair<>(node.getKey(), currentRate * node.getValue()));
                }
            }
        }
        throw new IllegalArgumentException("No conversion path found");
    }
}
