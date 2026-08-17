package com.restaurant.controller;

import com.restaurant.billing.CustomerAccount;
import com.restaurant.billing.Invoice;
import com.restaurant.billing.PaymentProcessor;
import com.restaurant.enums.PaymentMethod;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CheckoutController {

    @FXML
    private Label invoiceIdLabel;

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label serviceChargeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private ComboBox<String> paymentMethod;

    @FXML
    private Button confirmPaymentButton;

    @FXML
    private Button viewInvoiceButton;

    private Invoice invoice;

    private CustomerAccount customerAccount;

    private final PaymentProcessor paymentProcessor =
            new PaymentProcessor();

    @FXML
    public void initialize() {

        paymentMethod.getItems().add("Cash");
        paymentMethod.getItems().add("Credit Card");
        paymentMethod.getItems().add("Balance");
        paymentMethod.getItems().add("Loyalty Points");

        // Temporary data for GUI testing
        invoice = new Invoice(
                "INV-001",
                "ORD-001",
                1000.00
        );

        customerAccount = new CustomerAccount(
                5000.00,
                2000
        );

        viewInvoiceButton.setDisable(true);

        displayInvoice();
    }

    private void displayInvoice() {

        invoiceIdLabel.setText(
                "Invoice ID: " + invoice.getInvoiceId()
        );

        orderIdLabel.setText(
                "Order ID: " + invoice.getOrderId()
        );

        subtotalLabel.setText(
                String.format(
                        "Subtotal: %.2f EGP",
                        invoice.getSubtotal()
                )
        );

        taxLabel.setText(
                String.format(
                        "Tax: %.2f EGP",
                        invoice.getTax()
                )
        );

        serviceChargeLabel.setText(
                String.format(
                        "Service Charge: %.2f EGP",
                        invoice.getServiceCharge()
                )
        );

        totalLabel.setText(
                String.format(
                        "Total: %.2f EGP",
                        invoice.getTotal()
                )
        );
    }

    @FXML
    public void confirmPayment() {

        if (invoice.isPaid()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment",
                    "This invoice has already been paid."
            );

            return;
        }

        if (paymentMethod.getValue() == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment",
                    "Please select a payment method."
            );

            return;
        }

        PaymentMethod selectedMethod;

        switch (paymentMethod.getValue()) {

            case "Cash":
                selectedMethod = PaymentMethod.CASH;
                break;

            case "Credit Card":
                selectedMethod = PaymentMethod.CREDIT_CARD;
                break;

            case "Balance":
                selectedMethod = PaymentMethod.BALANCE;
                break;

            case "Loyalty Points":
                selectedMethod =
                        PaymentMethod.LOYALTY_POINTS;
                break;

            default:
                showAlert(
                        Alert.AlertType.ERROR,
                        "Payment",
                        "Invalid payment method."
                );
                return;
        }

        boolean successful =
                paymentProcessor.processPayment(
                        invoice,
                        customerAccount,
                        selectedMethod
                );

        if (successful) {

            confirmPaymentButton.setDisable(true);

            viewInvoiceButton.setDisable(false);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Payment",
                    "Payment confirmed successfully!\n\n"
                            + "Invoice: "
                            + invoice.getInvoiceId()
                            + "\n"
                            + "Payment Method: "
                            + paymentMethod.getValue()
                            + "\n"
                            + "Total: "
                            + String.format(
                            "%.2f",
                            invoice.getTotal()
                    )
                            + " EGP"
            );

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Payment Failed",
                    "Payment could not be completed.\n\n"
                            + "Please check the customer's "
                            + "balance or loyalty points."
            );
        }
    }

    @FXML
    public void viewInvoice() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/invoice.fxml"
                            )
                    );

            Parent root = loader.load();

            InvoiceController controller =
                    loader.getController();

            controller.setInvoice(invoice);

            Stage stage = new Stage();

            stage.setTitle("Invoice");

            stage.setScene(
                    new Scene(root)
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invoice",
                    "Could not open the invoice screen."
            );
        }
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}