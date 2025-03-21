package org.poo.models;

import lombok.Getter;
import org.poo.fileio.CommandInput;

@Getter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final CommandInput input) {
        super(input);
        interestRate = input.getInterestRate();
    }

    @Override
    public void addInterest() {
        double interest = getBalance() * interestRate;
        setBalance(getBalance() + interest);
    }

    @Override
    public void changeInterestRate(final double newRate) {
        interestRate = newRate;
    }
}
