package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.model.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ReservationController {

    @FXML private ListView<Reservation> reservationListView;
    private ObservableList<Reservation> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        reservationListView.setItems(items);
        refresh();
    }

    private void refresh() {
        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) { SceneNavigator.navigateTo("login.fxml"); return; }
        items.setAll(customer.getMyReservations());
    }

    @FXML
    public void handleCancel() {
        Reservation selected = reservationListView.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.error("Error", "Please select a reservation to cancel."); return; }

        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        try {
            customer.cancelReservation(selected.getReservationId());
            AlertHelper.info("Cancelled", "Reservation " + selected.getReservationId() + " has been cancelled.");
            refresh();
        } catch (Exception e) {
            AlertHelper.error("Error", e.getMessage());
        }
    }

    @FXML public void handleBack() { SceneNavigator.navigateTo("customer_dashboard.fxml"); }
}
