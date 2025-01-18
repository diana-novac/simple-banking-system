package org.poo.plans;

public final class StudentPlan implements AccountPlan {
    @Override
    public String getPlanName() {
        return "student";
    }
}
