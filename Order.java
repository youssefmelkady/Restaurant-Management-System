package com.restaurant.model;

import com.restaurant.enums.OrderStatus;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String      orderId;
    private Customer    customer;
    private Table       table;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order(String orderId, Customer customer, Table table) {
        this.orderId  = orderId;
        this.customer = customer;
        this.table    = table;
        this.items    = new ArrayList<>();
        this.status   = OrderStatus.PLACED;
        if (orderId == null || orderId.trim().isEmpty())
            throw new IllegalArgumentException("Order ID cannot be empty.");

        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null.");

        if (table == null)
            throw new IllegalArgumentException("Table cannot be null.");
    }

    // Only adds available items
    public boolean addItem(OrderItem item) {
        if (item != null && item.getMenuItem() != null && item.getMenuItem().isAvailable()) {
            items.add(item);
            return true;
        }
        return false;
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) total += item.getTotalPrice();
        return total;
    }

    public String      getOrderId()  { return orderId; }
    public Customer    getCustomer() { return customer; }
    public Table       getTable()    { return table; }
    public List<OrderItem> getItems(){ return items; }
    public OrderStatus getStatus()   { return status; }
    public void        setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Order #" + orderId + " | Table: " + table.getTableNumber()
                + " | Items: " + items.size() + " | Total: " + String.format("%.2f", calculateTotal())
                + " EGP | " + status;
    }
}
