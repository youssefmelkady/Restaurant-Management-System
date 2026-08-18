package com.restaurant.model;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.Role;
import com.restaurant.enums.TableStatus;
import java.time.LocalDate;
import java.util.List;

public class Waiter extends Staff {

    public Waiter(String id, String username, String password, LocalDate dateOfBirth, int workingHours) {
        super(id, username, password, dateOfBirth, Role.WAITER, workingHours);
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        for (Order o : RestaurantDatabase.orders) {
            if (o.getOrderId().equals(orderId)) {
                o.setStatus(newStatus);
                // If PAID, free the table
                if (newStatus == OrderStatus.PAID) {
                    o.getTable().setStatus(TableStatus.AVAILABLE);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Order not found: " + orderId);
    }

    public Order takeOrder(String orderId, Customer customer, Table table) {
        Order order = new Order(orderId, customer, table);
        RestaurantDatabase.orders.add(order);
        table.setStatus(TableStatus.OCCUPIED);
        return order;
    }

    public List<Order> getAllOrders() { return RestaurantDatabase.orders; }
    public List<Table> getAllTables() { return RestaurantDatabase.tables; }
    public List<Reservation> getAllReservations() { return RestaurantDatabase.reservations; }
}
