package org.poo.models;

import org.poo.fileio.CommandInput;

/**
 * Factory class for creating Account objects
 */
public final class AccountFactory {
    private AccountFactory() {

    }
    /**
     * Creates an Account object using the provided input
     *
     * @param input CommandInput containing the configuration for the account
     * @return A new Account instance
     */
    public static Account createAccount(final CommandInput input) {
        return switch (input.getAccountType()) {
            case "classic" -> new Account(input);
            case "savings" -> new SavingsAccount(input);
            case "business" -> new BusinessAccount(input);
            default -> null;
        };
    }
}
