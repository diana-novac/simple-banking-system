package org.poo.commerciants;

import org.poo.main.App;
import org.poo.models.Account;
import org.poo.models.User;

/**
 * Interface for defining a cashback strategy
 * Provides methods to determine eligibility and apply cashback
 */
public interface CashbackStrategy {

    /**
     * Determines if a user is eligible for cashback based on the given parameters
     *
     * @param app      The application context
     * @param user     The user making the transaction
     * @param account  The account from which the transaction is made
     * @param category The category of the commerciant
     * @return true if the user is eligible for cashback, false otherwise
     */
    boolean isEligible(App app, User user, Account account, String category);

    /**
     * Applies cashback to the user's account if they are eligible
     *
     * @param app               The application context
     * @param user              The user receiving the cashback
     * @param account           The account involved in the transaction
     * @param category          The category of the commerciant
     * @param transactionAmount The amount of the transaction
     * @param commerciant       The commerciant associated with the transaction
     */
    void applyCashback(App app, User user, Account account, String category,
                       double transactionAmount, Commerciant commerciant);

    /**
     * Retrieves the type of cashback strategy implemented
     *
     * @return A String representing the type of cashback strategy
     */
    String getType();
}
