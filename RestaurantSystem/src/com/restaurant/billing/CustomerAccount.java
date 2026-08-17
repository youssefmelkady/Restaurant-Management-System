package com.restaurant.billing;

public class CustomerAccount {

    //data field
    private double balance;
    private int loyaltyPoints;

    //constructor(with parameters)
    public CustomerAccount(double balance, int loyaltyPoints) {

        //invalid starting exception(balance)
        if (balance < 0) {
            throw new IllegalArgumentException(
                    "Balance cannot be negative"
            );
        }

        //invalid starting exception(loyalty Points)
        if (loyaltyPoints < 0) {
            throw new IllegalArgumentException(
                    "Loyalty points cannot be negative"
            );
        }

        //assign
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
    }

    //getters
    public double getBalance() {
        return balance;
    }
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    //methods
    public boolean deductBalance(double amount) {

        //checking balance
        if (amount <= 0) {
            return false;
        }

        if (amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }



    public boolean deductLoyaltyPoints(int points) {

        //checking points
        if (points <= 0) {
            return false;
        }

        if (points > loyaltyPoints) {
            return false;
        }

        loyaltyPoints -= points;
        return true;
    }
}