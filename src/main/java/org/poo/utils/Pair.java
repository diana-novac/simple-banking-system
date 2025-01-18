package org.poo.utils;

import lombok.Getter;

/**
 * A generic utility class to represent a pair of values
 *
 * @param <K> The type of the first value (key)
 * @param <V> The type of the second value (value)
 */
@Getter
public class Pair<K, V> {
    private final K key;
    private final V value;

    /**
     * Constructs a Pair instance with the specified key and value
     *
     * @param key   The first value of the pair
     * @param value The second value of the pair
     */
    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
}
