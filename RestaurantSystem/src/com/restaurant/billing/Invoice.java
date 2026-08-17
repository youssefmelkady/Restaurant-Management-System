package src.com.restaurant.billing;

import src.com.restaurant.enums.PaymentMethod;
import src.com.restaurant.interfaces.Payable;

public class Invoice implements Payable {

    private String invoiceId;
    private String orderId;

    private double subtotal;
    private double tax;
    private double serviceCharge;
    private double total;

    private boolean paid;
    private PaymentMethod paymentMethod;

    private static final double TAX_RATE = 0.14;
    private static final double SERVICE_CHARGE_RATE = 0.12;

    public Invoice(String invoiceId, String orderId, double subtotal) {

        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Invoice ID cannot be empty"
            );
        }

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Order ID cannot be empty"
            );
        }

        if (subtotal < 0) {
            throw new IllegalArgumentException(
                    "Subtotal cannot be negative"
            );
        }

        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.paid = false;

        calculateCharges();
    }

    private void calculateCharges() {

        tax = subtotal * TAX_RATE;
        serviceCharge = subtotal * SERVICE_CHARGE_RATE;
        total = subtotal + tax + serviceCharge;
    }

    @Override
    public double calculateTotal() {
        return total;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public double getTotal() {
        return total;
    }

    public boolean isPaid() {
        return paid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void markAsPaid(PaymentMethod paymentMethod) {

        if (this.paid) {
            return;
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException(
                    "Payment method cannot be null"
            );
        }

        this.paymentMethod = paymentMethod;
        this.paid = true;
    }
}