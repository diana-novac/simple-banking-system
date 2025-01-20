package org.poo.plans;

import org.poo.main.App;

import static org.poo.utils.Constants.*;

/**
 * Interface representing the structure and behavior of an account plan
 * Provides default implementations for common functionalities like transaction fees,
 * automatic upgrades, and cashback rates based on spending thresholds
 */
public interface AccountPlan {

    /**
     * Calculates the transaction fee percentage for a specific transaction
     *
     * @param app     The application context
     * @param amount  The amount for which the transaction fee is calculated
     * @return The transaction fee percentage
     */
    default double getTransactionFee(final App app, final double amount) {
        return 0.0;
    }

    /**
     * Determines whether the account is eligible for an automatic upgrade
     * based on the number of transactions made
     *
     * @param transactionCount The number of transactions made by the account
     * @return true if eligible for automatic plan upgrade; false otherwise
     */
    default boolean automaticUpgrade(final int transactionCount) {
        return false;
    }

    /**
     * Calculates the cashback rate based on the total spending amount
     *
     * @param totalSpendingAmount The total spending amount for the account
     * @return The cashback rate
     */
    default double getCashbackRate(final double totalSpendingAmount) {
        if (totalSpendingAmount >= THIRD_THRESHOLD) {
            return STANDARD_BIG_CASHBACK_RATE;
        } else if (totalSpendingAmount >= SECOND_THRESHOLD) {
            return STANDARD_MEDIUM_CASHBACK_RATE;
        } else if (totalSpendingAmount >= FIRST_THRESHOLD) {
            return STANDARD_SMALL_CASHBACK_RATE;
        }
        return 0.0;
    }

    /**
     * Retrieves the name of the account plan
     *
     * @return The name of the plan as a String
     */
    String getPlanName();
}
