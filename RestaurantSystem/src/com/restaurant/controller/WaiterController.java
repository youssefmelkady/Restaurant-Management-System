package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;
import com.restaurant.model.Waiter;
import com.restaurant.network.OrderStatusClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class WaiterController {

    @FXML private ListView<Order>       orderListView;
    @FXML private ComboBox<OrderStatus> statusComboBox;
    @FXML private TextArea              liveFeedArea;

    private Waiter waiter;
    private OrderStatusClient client;

    @FXML
    public void initialize() {
        waiter = (Waiter) SessionManager.getInstance().getCurrentStaff();
        if (waiter == null) {
            SceneNavigator.navigateTo("login.fxml");
            return;
        }

        statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
        refreshOrders();

        try {
            client = new OrderStatusClient("localhost", 5001, msg ->
                    javafx.application.Platform.runLater(() -> liveFeedArea.appendText(msg + "\n")));
            client.connect();
        } catch (Exception e) {
            liveFeedArea.setText("Live feed unavailable.\n");
        }
    }

    private void refreshOrders() {
        orderListView.setItems(FXCollections.observableArrayList(RestaurantDatabase.orders));
    }

    @FXML
    public void handleUpdateStatus() {
        Order selected = orderListView.getSelectionModel().getSelectedItem();
        OrderStatus newStatus = statusComboBox.getValue();

        if (selected == null || newStatus == null) {
            AlertHelper.error("Error", "Select an order and a new status.");
            return;
        }

        try {
            waiter.updateOrderStatus(selected.getOrderId(), newStatus);
            String update = "Order " + selected.getOrderId() + " updated to " + newStatus;
            if (client != null) client.sendUpdate(update);
            refreshOrders();
            AlertHelper.info("Updated", update);
        } catch (Exception e) {
            AlertHelper.error("Error", e.getMessage());
        }
    }

    @FXML
    public void handleLogout() {
        if (client != null) client.disconnect();
        SessionManager.getInstance().logout();
        SceneNavigator.navigateTo("login.fxml");
    }
}
