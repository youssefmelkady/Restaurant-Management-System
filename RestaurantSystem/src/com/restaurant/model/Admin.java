package com.restaurant.model;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.Role;
import com.restaurant.enums.TableStatus;
import com.restaurant.interfaces.Manageable;
import java.time.LocalDate;
import java.util.List;

public class Admin extends Staff implements Manageable<Table> {

    public Admin(String id, String username, String password, LocalDate dateOfBirth, int workingHours) {
        super(id, username, password, dateOfBirth, Role.ADMIN, workingHours);
    }

    // Manageable<Table> contract
    @Override
    public void add(Table table) {
        RestaurantDatabase.tables.add(table);
    }

    @Override
    public void update(Table table) {
        for (Table t : RestaurantDatabase.tables) {
            if (t.getTableNumber().equals(table.getTableNumber())) {
                t.setStatus(table.getStatus());
                return;
            }
        }
    }

    @Override
    public void delete(String tableNumber) {
        RestaurantDatabase.tables.removeIf(t -> t.getTableNumber().equals(tableNumber));
    }

    /*
     * It returns
     * the actual list,
     * not a copy.
     */

    @Override
    public List<Table> getAll() {
        return RestaurantDatabase.tables;
    }

    // ---- Table CRUD ----
    public void addTable(Table table) {
        add(table);
    }

    public void deleteTable(String tableNumber) {
        delete(tableNumber);
    }

    public void updateTableStatus(String tableNumber, TableStatus status) {
        for (Table t : RestaurantDatabase.tables) {
            if (t.getTableNumber().equals(tableNumber)) {
                t.setStatus(status);
                return;
            }
        }
    }

    public List<Table> getAllTables() {
        return RestaurantDatabase.tables;
    }

    // ---- MenuItem CRUD ----
    public void addMenuItem(MenuItem item) {
        RestaurantDatabase.menuItems.add(item);
    }

    public void deleteMenuItem(String itemId) {
        RestaurantDatabase.menuItems.removeIf(m -> m.getItemId().equals(itemId));
    }

    public void toggleMenuItemAvailability(String itemId) {
        for (MenuItem m : RestaurantDatabase.menuItems) {
            if (m.getItemId().equals(itemId)) {
                m.setAvailable(!m.isAvailable());
                return;
            }
        }
    }

    public List<MenuItem> getAllMenuItems() {
        return RestaurantDatabase.menuItems;
    }

    // ---- MenuCategory CRUD ----
    public void addCategory(MenuCategory category) {
        RestaurantDatabase.categories.add(category);
    }

    public void deleteCategory(String categoryId) {
        RestaurantDatabase.categories.removeIf(c -> c.getCategoryId().equals(categoryId));
    }

    public List<MenuCategory> getAllCategories() {
        return RestaurantDatabase.categories;
    }

    // ---- View all customers / orders ----
    public List<Customer> getAllCustomers() {
        return RestaurantDatabase.customers;
    }

    public List<Order> getAllOrders() {
        return RestaurantDatabase.orders;
    }

    public List<Reservation> getAllReservations() {
        return RestaurantDatabase.reservations;
    }
}