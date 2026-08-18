package com.restaurant.model;

import com.restaurant.enums.TableStatus;

public class Table {
    private String tableNumber;
    private int capacity;
    private String location; // Indoor, Outdoor, VIP
    private TableStatus status;

    public Table(String tableNumber, int capacity, String location, TableStatus status) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Table capacity must be greater than zero");
        }
        if (status == null) {
            throw new IllegalArgumentException("Table status cannot be null");
        }
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    public String getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }
    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Table " + tableNumber + " | " + location + " | Seats: " + capacity + " | " + status;
    }
}
