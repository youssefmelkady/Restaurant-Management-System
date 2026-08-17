package com.restaurant.controller;

import src.com.restaurant.billing.Invoice;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InvoiceController {

    @FXML
    private Label invoiceIdLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label serviceChargeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label paymentStatusLabel;

    @FXML
    private Label paymentMethodLabel;

    @FXML
    private Button backButton;

    private Invoice invoice;

    public void setInvoice(Invoice invoice) {

        this.invoice = invoice;

        if (invoice == null) {
            return;
        }

        invoiceIdLabel.setText(
                String.valueOf(invoice.getInvoiceId())
        );

        subtotalLabel.setText(
                String.format(
                        "%.2f EGP",
                        invoice.getSubtotal()
                )
        );

        taxLabel.setText(
                String.format(
                        "%.2f EGP",
                        invoice.getTax()
                )
        );

        serviceChargeLabel.setText(
                String.format(
                        "%.2f EGP",
                        invoice.getServiceCharge()
                )
        );

        totalLabel.setText(
                String.format(
                        "%.2f EGP",
                        invoice.calculateTotal()
                )
        );

        paymentStatusLabel.setText(
                invoice.isPaid()
                        ? "Paid"
                        : "Unpaid"
        );

        if (invoice.isPaid()) {

            paymentMethodLabel.setText(
                    String.valueOf(
                            invoice.getPaymentMethod()
                    )
            );

        } else {

            paymentMethodLabel.setText("-");
        }
    }

    @FXML
    private void handleBack() {

        if (backButton != null) {

            backButton
                    .getScene()
                    .getWindow()
                    .hide();
        }
    }
}