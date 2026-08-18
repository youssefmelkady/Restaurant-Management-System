package com.restaurant.controller;

import com.restaurant.billing.CustomerAccount;
import com.restaurant.billing.Invoice;
import com.restaurant.billing.PaymentProcessor;
import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.TableStatus;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.model.Customer;
import com.restaurant.model.Order;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutController {

    @FXML private ComboBox<Order> orderComboBox;
    @FXML private Label           invoiceIdLabel;
    @FXML private Label           orderIdLabel;
    @FXML private Label           subtotalLabel;
    @FXML private Label           taxLabel;
    @FXML private Label           serviceChargeLabel;
    @FXML private Label           totalLabel;
    @FXML private RadioButton     rbCash;
    @FXML private RadioButton     rbCard;
    @FXML private RadioButton     rbBalance;
    @FXML private RadioButton     rbLoyalty;
    @FXML private ToggleGroup     paymentGroup;
    @FXML private TextArea        invoiceArea;

    private Invoice         currentInvoice;
    private CustomerAccount currentAccount;
    private final PaymentProcessor paymentProcessor = new PaymentProcessor();

    @FXML
    public void initialize() {
        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneNavigator.navigateTo("login.fxml");
            return;
        }

        List<Order> unpaid = RestaurantDatabase.orders.stream()
                .filter(o -> o.getCustomer() != null &&
                        o.getCustomer().getId().equals(customer.getId()))
                .filter(o -> o.getStatus() != OrderStatus.PAID)
                .collect(Collectors.toList());

        orderComboBox.setItems(FXCollections.observableArrayList(unpaid));

        orderComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Order o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null :
                        "Order " + o.getOrderId() + " — " +
                                o.getItems().size() + " items — " +
                                String.format("%.2f EGP", o.calculateTotal()));
            }
        });

        orderComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Order o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? "Select an order..." :
                        "Order " + o.getOrderId() + " — " +
                                String.format("%.2f EGP", o.calculateTotal()));
            }
        });

        orderComboBox.setOnAction(e -> buildInvoice());
    }

    private void buildInvoice() {
        Order selected = orderComboBox.getValue();
        if (selected == null) return;

        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        String invoiceId = RestaurantDatabase.nextInvoiceId();

        currentInvoice = new Invoice(invoiceId, selected.getOrderId(), selected.calculateTotal());
        currentAccount = new CustomerAccount(customer.getBalance(), customer.getLoyaltyPoints());

        invoiceIdLabel.setText(currentInvoice.getInvoiceId());
        orderIdLabel.setText(currentInvoice.getOrderId());
        subtotalLabel.setText(String.format("%.2f EGP", currentInvoice.getSubtotal()));
        taxLabel.setText(String.format("%.2f EGP", currentInvoice.getTax()));
        serviceChargeLabel.setText(String.format("%.2f EGP", currentInvoice.getServiceCharge()));
        totalLabel.setText(String.format("%.2f EGP", currentInvoice.getTotal()));

        invoiceArea.setText(
                "===== INVOICE " + currentInvoice.getInvoiceId() + " =====\n" +
                        "Order       : " + currentInvoice.getOrderId() + "\n" +
                        String.format("Subtotal     : %.2f EGP\n", currentInvoice.getSubtotal()) +
                        String.format("Tax (14%%)   : %.2f EGP\n", currentInvoice.getTax()) +
                        String.format("Service (12%%): %.2f EGP\n", currentInvoice.getServiceCharge()) +
                        "-------------------------\n" +
                        String.format("TOTAL        : %.2f EGP\n", currentInvoice.getTotal()) +
                        "Status: UNPAID"
        );
    }

    @FXML
    public void confirmPayment() {
        if (currentInvoice == null) {
            AlertHelper.error("Error", "Please select an order first.");
            return;
        }
        if (currentInvoice.isPaid()) {
            AlertHelper.info("Already Paid", "This order has already been paid.");
            return;
        }

        PaymentMethod method = getSelectedMethod();
        if (method == null) {
            AlertHelper.error("Error", "Please select a payment method.");
            return;
        }

        boolean success = paymentProcessor.processPayment(currentInvoice, currentAccount, method);

        if (success) {
            Customer customer = SessionManager.getInstance().getCurrentCustomer();

            if (method == PaymentMethod.BALANCE) {
                customer.setBalance(currentAccount.getBalance());
            } else if (method == PaymentMethod.LOYALTY_POINTS) {
                int used = (int) Math.ceil(currentInvoice.calculateTotal());
                customer.deductLoyaltyPoints(used);
            }

            int pointsEarned = (int)(currentInvoice.getSubtotal() / 10);
            customer.addLoyaltyPoints(pointsEarned);

            Order selected = orderComboBox.getValue();
            if (selected != null) {
                selected.setStatus(OrderStatus.PAID);
                if (selected.getTable() != null
                        && selected.getTable().getStatus() == TableStatus.OCCUPIED) {
                    selected.getTable().setStatus(TableStatus.AVAILABLE);
                }
            }

            RestaurantDatabase.invoices.add(currentInvoice);

            invoiceArea.setText(
                    "===== INVOICE " + currentInvoice.getInvoiceId() + " =====\n" +
                            "Order       : " + currentInvoice.getOrderId() + "\n" +
                            String.format("Subtotal     : %.2f EGP\n", currentInvoice.getSubtotal()) +
                            String.format("Tax (14%%)   : %.2f EGP\n", currentInvoice.getTax()) +
                            String.format("Service (12%%): %.2f EGP\n", currentInvoice.getServiceCharge()) +
                            "-------------------------\n" +
                            String.format("TOTAL        : %.2f EGP\n", currentInvoice.getTotal()) +
                            "Status: PAID via " + method
            );

            AlertHelper.info("Payment Confirmed",
                    "Invoice " + currentInvoice.getInvoiceId() + " paid successfully!\n" +
                            "Payment Method: " + method + "\n" +
                            "Loyalty Points Earned: " + pointsEarned);
        } else {
            AlertHelper.error("Payment Failed",
                    "Payment could not be completed.\n" +
                            "Please check your balance or loyalty points.");
        }
    }

    private PaymentMethod getSelectedMethod() {
        if (rbCash.isSelected())    return PaymentMethod.CASH;
        if (rbCard.isSelected())    return PaymentMethod.CREDIT_CARD;
        if (rbBalance.isSelected()) return PaymentMethod.BALANCE;
        if (rbLoyalty.isSelected()) return PaymentMethod.LOYALTY_POINTS;
        return null;
    }

    @FXML
    public void handleBack() {
        SceneNavigator.navigateTo("customer_dashboard.fxml");
    }
}
