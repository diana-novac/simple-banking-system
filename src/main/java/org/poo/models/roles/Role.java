package org.poo.models.roles;

import org.poo.models.Account;

/**
 * Represents a role associated with a user in a business account
 * Defines permissions and behaviors for account-related actions
 * such as setting limits and performing transactions
 */
public interface Role {

    /**
     * Checks if the role has permission to set limits (spending or deposit limits)
     *
     * @return true if the role can set limits, false otherwise
     */
    boolean canSetLimits();

    /**
     * Checks if the role has permission to perform a specific transaction
     *
     * @param amount  The amount involved in the transaction
     * @param type    The type of transaction (e.g., "deposit", "spending")
     * @param account The account on which the transaction is attempted
     * @return true if the role can perform the transaction, false otherwise
     */
    boolean canPerformTransaction(double amount, String type, Account account);

    /**
     * Retrieves the type of the role
     *
     * @return A String representing the role type
     */
    String getType();
}
