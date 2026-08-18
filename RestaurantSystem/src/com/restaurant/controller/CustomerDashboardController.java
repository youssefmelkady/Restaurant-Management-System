package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.model.Order;
import com.restaurant.model.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class CustomerDashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label balanceLabel;
    @FXML private Label loyaltyLabel;
    @FXML private Label dietaryLabel;
    @FXML private ListView<String> reservationsList;
    @FXML private ListView<String> ordersList;

    @FXML
    public void initialize() {
        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneNavigator.navigateTo("login.fxml");
            return;
        }

        usernameLabel.setText(customer.getUsername());
        balanceLabel.setText(String.format("%.2f EGP", customer.getBalance()));
        loyaltyLabel.setText(customer.getLoyaltyPoints() + " pts");
        dietaryLabel.setText(customer.getDietaryPreferences());

        refreshReservations(customer);
        refreshOrders(customer);
    }

    private void refreshReservations(Customer customer) {
        reservationsList.getItems().clear();
        for (Reservation r : customer.getMyReservations())
            reservationsList.getItems().add(r.toString());
        if (reservationsList.getItems().isEmpty())
            reservationsList.getItems().add("No upcoming reservations.");
    }

    private void refreshOrders(Customer customer) {
        ordersList.getItems().clear();
        for (Order o : customer.getMyOrders())
            ordersList.getItems().add(o.toString());
        if (ordersList.getItems().isEmpty())
            ordersList.getItems().add("No orders yet.");
    }

    @FXML public void handleBrowseTables()       { SceneNavigator.navigateTo("tables.fxml"); }
    @FXML public void handleBrowseMenu()         { SceneNavigator.navigateTo("menu.fxml"); }
    @FXML public void handleManageReservations() { SceneNavigator.navigateTo("reservation.fxml"); }
    @FXML public void handleCheckout()           { SceneNavigator.navigateTo("checkout.fxml"); }

    @FXML
    public void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigateTo("login.fxml");
    }
}
