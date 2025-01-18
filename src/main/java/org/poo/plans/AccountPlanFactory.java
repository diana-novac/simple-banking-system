package org.poo.plans;

public final class AccountPlanFactory {
    private AccountPlanFactory() { }

    public static AccountPlan createPlan(String planName) {
        return switch (planName.toLowerCase()) {
            case "standard" -> new StandardPlan();
            case "student" -> new StudentPlan();
            case "silver" -> new SilverPlan();
            case "gold" -> new GoldPlan();
            default -> throw new IllegalArgumentException();
        };
    }
}
