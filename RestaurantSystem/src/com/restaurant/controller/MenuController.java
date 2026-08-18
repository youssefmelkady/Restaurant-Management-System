package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.model.Customer;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Table;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MenuController {

    @FXML private ComboBox<MenuCategory> categoryComboBox;
    @FXML private ListView<MenuItem> menuListView;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField notesField;

    @FXML private TableView<OrderItem> orderTableView;
    @FXML private TableColumn<OrderItem, String> colItemName;
    @FXML private TableColumn<OrderItem, Integer> colQty;
    @FXML private TableColumn<OrderItem, Double> colUnitPrice;
    @FXML private TableColumn<OrderItem, Double> colTotal;
    @FXML private TableColumn<OrderItem, String> colNotes;

    @FXML private Label runningTotalLabel;

    @FXML private TextField newItemName;
    @FXML private TextField newItemPrice;
    @FXML private TextField newItemDesc;
    @FXML private CheckBox newItemAvailable;

    private ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();
    private Task<Void> currentLoadTask;

    @FXML
    public void initialize() {
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        orderTableView.setItems(currentOrderItems);
        loadMenuAsync();
    }

    @FXML
    public void handleAsyncRefresh() {
        loadMenuAsync();
    }

    private void loadMenuAsync() {
        if (currentLoadTask != null && currentLoadTask.isRunning()) {
            currentLoadTask.cancel();
        }

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(400);
                Platform.runLater(() -> {
                    categoryComboBox.getItems().setAll(RestaurantDatabase.categories);
                    menuListView.getItems().setAll(RestaurantDatabase.menuItems);
                });
                return null;
            }
        };
        currentLoadTask = loadTask;
        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void handleCategoryChange() {
        MenuCategory selected = categoryComboBox.getValue();
        if (selected == null) {
            menuListView.getItems().setAll(RestaurantDatabase.menuItems);
        } else {
            List<MenuItem> filtered = RestaurantDatabase.menuItems.stream()
                    .filter(item -> item.getCategory() != null &&
                            item.getCategory().getCategoryId().equals(selected.getCategoryId()))
                    .collect(Collectors.toList());
            menuListView.getItems().setAll(filtered);
        }
    }

    @FXML
    public void handleAddToOrder() {
        MenuItem selectedItem = menuListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            AlertHelper.error("Selection Error", "Please select an item from the menu.");
            return;
        }

        if (!selectedItem.isAvailable()) {
            AlertHelper.error("Item Unavailable", selectedItem.getName() + " is currently unavailable.");
            return;
        }

        int qty = quantitySpinner.getValue();
        String notes = notesField.getText();

        currentOrderItems.add(new OrderItem(selectedItem, qty, notes));
        updateRunningTotal();

        notesField.clear();
        quantitySpinner.getValueFactory().setValue(1);
    }

    private void updateRunningTotal() {
        double total = 0;
        for (OrderItem item : currentOrderItems) {
            total += item.getTotalPrice();
        }
        runningTotalLabel.setText(String.format("%.2f EGP", total));
    }

    @FXML
    public void handleClearOrder() {
        currentOrderItems.clear();
        updateRunningTotal();
    }

    @FXML
    public void handleSubmitOrder() {
        if (currentOrderItems.isEmpty()) {
            AlertHelper.error("Order Empty", "Please add items before submitting.");
            return;
        }

        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            AlertHelper.error("Not Logged In", "Please log in to place an order.");
            return;
        }

        Table table = RestaurantDatabase.tables.stream()
                .filter(t -> t.getStatus() == com.restaurant.enums.TableStatus.AVAILABLE)
                .findFirst().orElse(null);

        if (table == null) {
            AlertHelper.error("No Table Available", "There is no available table for this order.");
            return;
        }

        Order newOrder = new Order(RestaurantDatabase.nextOrderId(), customer, table);
        for (OrderItem item : currentOrderItems) {
            newOrder.addItem(item);
        }

        RestaurantDatabase.orders.add(newOrder);
        table.setStatus(com.restaurant.enums.TableStatus.OCCUPIED);
        AlertHelper.info("Order Placed", "Your order has been placed. Order ID: " + newOrder.getOrderId());
        handleClearOrder();
    }

    @FXML
    public void handleAdminAddItem() {
        String name = newItemName.getText().trim();
        String priceText = newItemPrice.getText().trim();
        String desc = newItemDesc.getText().trim();
        boolean available = newItemAvailable.isSelected();
        MenuCategory category = categoryComboBox.getValue();

        if (name.isEmpty() || priceText.isEmpty() || category == null) {
            AlertHelper.error("Validation Error", "Name, price, and category are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                AlertHelper.error("Input Error", "Price cannot be negative.");
                return;
            }
            MenuItem newItem = new MenuItem(
                    RestaurantDatabase.nextMenuItemId(), name, price, desc, category, available);
            RestaurantDatabase.menuItems.add(newItem);
            handleCategoryChange();
            newItemName.clear();
            newItemPrice.clear();
            newItemDesc.clear();
            AlertHelper.info("Success", "Item added to menu.");
        } catch (NumberFormatException e) {
            AlertHelper.error("Input Error", "Price must be a valid number.");
        }
    }

    @FXML
    public void handleBack() {
        if (SessionManager.getInstance().getCurrentCustomer() != null) {
            SceneNavigator.navigateTo("customer_dashboard.fxml");
        } else {
            SceneNavigator.navigateTo("login.fxml");
        }
    }
}
