package com.restaurant.billing;

import com.restaurant.enums.PaymentMethod;

public class PaymentProcessor {

    private static final int POINTS_PER_EGP = 10;

    public boolean processPayment(
            Invoice invoice,
            CustomerAccount account,
            PaymentMethod method) {

        //throwing exception when a value cannot be null
        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice cannot be null"
            );
        }

        if (account == null) {
            throw new IllegalArgumentException(
                    "Customer account cannot be null"
            );
        }

        if (method == null) {
            throw new IllegalArgumentException(
                    "Payment method cannot be null"
            );
        }

        //pervent the invoice to paid twice
        if (invoice.isPaid()) {
            return false;
        }

        boolean successful = false;

        if (method == PaymentMethod.CASH) {

            successful = true;

        } else if (method == PaymentMethod.CREDIT_CARD) {

            successful = true;

        } else if (method == PaymentMethod.BALANCE) {

            //deduct from user balance
            successful =
                    account.deductBalance(
                            invoice.calculateTotal()
                    );

        } else if (method == PaymentMethod.LOYALTY_POINTS) {

            //check the required poinst is effiecent
            int requiredPoints =
                    (int) Math.ceil(
                            invoice.calculateTotal()
                                    * POINTS_PER_EGP
                    );

            //deduct from user loyalty points
            successful =
                    account.deductLoyaltyPoints(
                            requiredPoints
                    );
        }

        //submitting paid
        if (successful) {
            invoice.markAsPaid(method);
        }

        return successful;
    }
}