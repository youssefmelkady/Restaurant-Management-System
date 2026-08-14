package com.restaurant.gui;

import com.restaurant.model.Customer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label idLabel;
    @FXML private Label phoneLabel;
    @FXML private Label dobLabel;
    @FXML private Label dietaryLabel;
    @FXML private Label balanceLabel;
    @FXML private Label loyaltyLabel;
    @FXML private ListView<String> reservationsList;
    @FXML private Label messageLabel;

    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;

        if (customer == null) {
            return;
        }

        usernameLabel.setText("Username: " + customer.getUsername());
        idLabel.setText("Customer ID: " + customer.getId());
        phoneLabel.setText("Phone: " + customer.getPhoneNumber());
        dobLabel.setText("Date of Birth: " + customer.getDateOfBirth());
        dietaryLabel.setText("Dietary Preferences: " + customer.getDietaryPreferences());
        balanceLabel.setText(String.format("Balance: %.2f EGP", customer.getBalance()));
        loyaltyLabel.setText("Loyalty Points: " + customer.getLoyaltyPoints());

        loadReservations();
    }

    private void loadReservations() {
        reservationsList.getItems().clear();
        reservationsList.getItems().add("No upcoming reservations.");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Could not load login screen.");
        }
    }
}