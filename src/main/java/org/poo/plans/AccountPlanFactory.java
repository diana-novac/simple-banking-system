package org.poo.plans;

/**
 * Factory class for creating instances of AccountPlan
 * This factory provides an interface for creating different account plans
 * based on a given plan name
 */
public final class AccountPlanFactory {

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private AccountPlanFactory() { }

    /**
     * Creates and returns an instance of AccountPlan based on the provided plan name
     *
     * @param planName The name of the plan to create
     * @return An instance of AccountPlan corresponding to the provided plan name
     * @throws IllegalArgumentException if the provided plan name does not match any known plan
     */
    public static AccountPlan createPlan(final String planName) {
        return switch (planName.toLowerCase()) {
            case "standard" -> new StandardPlan();
            case "student" -> new StudentPlan();
            case "silver" -> new SilverPlan();
            case "gold" -> new GoldPlan();
            default -> throw new IllegalArgumentException("Invalid plan name: " + planName);
        };
    }
}
