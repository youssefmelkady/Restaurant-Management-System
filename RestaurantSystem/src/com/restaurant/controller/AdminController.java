package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.TableStatus;
import com.restaurant.model.Admin;
import com.restaurant.model.Customer;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.Reservation;
import com.restaurant.model.Table;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AdminController {

    // Tables tab
    @FXML private ListView<Table>        tableListView;
    @FXML private TextField              newTableCapacity;
    @FXML private ComboBox<String>       newTableLocation;

    // Menu Items tab
    @FXML private ListView<MenuItem>     menuItemListView;
    @FXML private TextField              newItemName;
    @FXML private TextField              newItemPrice;
    @FXML private TextField              newItemDesc;
    @FXML private ComboBox<MenuCategory> newItemCategory;
    @FXML private CheckBox               newItemAvailable;

    // Customers tab
    @FXML private ListView<String>       customersListView;
    @FXML private ListView<String>       ordersListView;
    @FXML private ListView<String>       reservationsListView;

    private Admin admin;

    @FXML
    public void initialize() {
        if (!(SessionManager.getInstance().getCurrentStaff() instanceof Admin)) {
            SceneNavigator.navigateTo("login.fxml");
            return;
        }
        admin = (Admin) SessionManager.getInstance().getCurrentStaff();

        newTableLocation.setItems(FXCollections.observableArrayList("Indoor", "Outdoor", "VIP"));
        refreshTables();
        refreshMenuItems();
        refreshOverview();
        newItemCategory.setItems(FXCollections.observableArrayList(RestaurantDatabase.categories));
    }

    // ---- Tables ----
    private void refreshTables() {
        tableListView.setItems(FXCollections.observableArrayList(RestaurantDatabase.tables));
    }

    @FXML public void handleAddTable() {
        String loc = newTableLocation.getValue();
        String capText = newTableCapacity.getText().trim();
        if (loc == null || capText.isEmpty()) { AlertHelper.error("Error", "Fill in all table fields."); return; }
        try {
            int cap = Integer.parseInt(capText);
            if (cap <= 0) {
                AlertHelper.error("Error", "Capacity must be greater than zero.");
                return;
            }
            Table t = new Table(RestaurantDatabase.nextTableId(), cap, loc, TableStatus.AVAILABLE);
            admin.addTable(t);
            refreshTables();
            newTableCapacity.clear();
            AlertHelper.info("Success", "Table added!");
        } catch (NumberFormatException e) {
            AlertHelper.error("Error", "Capacity must be a number.");
        }
    }

    @FXML public void handleDeleteTable() {
        Table selected = tableListView.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.error("Error", "Select a table to delete."); return; }
        admin.deleteTable(selected.getTableNumber());
        refreshTables();
        AlertHelper.info("Deleted", "Table " + selected.getTableNumber() + " removed.");
    }

    // ---- Menu Items ----
    private void refreshMenuItems() {
        menuItemListView.setItems(FXCollections.observableArrayList(RestaurantDatabase.menuItems));
    }

    @FXML public void handleAddMenuItem() {
        String name     = newItemName.getText().trim();
        String priceText= newItemPrice.getText().trim();
        String desc     = newItemDesc.getText().trim();
        MenuCategory cat= newItemCategory.getValue();
        boolean available = newItemAvailable.isSelected();

        if (name.isEmpty() || priceText.isEmpty() || cat == null) {
            AlertHelper.error("Error", "Name, price, and category are required.");
            return;
        }
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                AlertHelper.error("Error", "Price cannot be negative.");
                return;
            }
            MenuItem item = new MenuItem(RestaurantDatabase.nextMenuItemId(), name, price, desc, cat, available);
            admin.addMenuItem(item);
            refreshMenuItems();
            newItemName.clear(); newItemPrice.clear(); newItemDesc.clear();
            AlertHelper.info("Success", "Menu item added!");
        } catch (NumberFormatException e) {
            AlertHelper.error("Error", "Price must be a number.");
        }
    }

    @FXML public void handleDeleteMenuItem() {
        MenuItem selected = menuItemListView.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.error("Error", "Select a menu item to delete."); return; }
        admin.deleteMenuItem(selected.getItemId());
        refreshMenuItems();
        AlertHelper.info("Deleted", selected.getName() + " removed from menu.");
    }

    @FXML public void handleToggleAvailability() {
        MenuItem selected = menuItemListView.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.error("Error", "Select a menu item."); return; }
        admin.toggleMenuItemAvailability(selected.getItemId());
        refreshMenuItems();
    }

    // ---- Overview ----
    private void refreshOverview() {
        customersListView.getItems().clear();
        for (Customer c : RestaurantDatabase.customers)
            customersListView.getItems().add(c.getUsername() + " | Balance: " + c.getBalance() + " | Points: " + c.getLoyaltyPoints());

        ordersListView.getItems().clear();
        for (Order o : RestaurantDatabase.orders)
            ordersListView.getItems().add(o.toString());

        reservationsListView.getItems().clear();
        for (Reservation r : RestaurantDatabase.reservations)
            reservationsListView.getItems().add(r.toString());
    }

    @FXML public void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigateTo("login.fxml");
    }
}
