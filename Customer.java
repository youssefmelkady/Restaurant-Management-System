package com.restaurant.model;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.Role;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String phoneNumber;
    private double balance;
    private int loyaltyPoints;
    private String dietaryPreferences;

    public Customer(String id, String username, String password, LocalDate dateOfBirth,
                    String phoneNumber, double balance, int loyaltyPoints, String dietaryPreferences) {
        super(id, username, password, dateOfBirth, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
        this.dietaryPreferences = dietaryPreferences;
    }

    // --- Registration ---
    public static Customer register(String id, String username, String password,
                                    LocalDate dob, String phone, String dietary) {
        if (!isValidPassword(password))
            throw new IllegalArgumentException("Weak password! Min 8 chars, 1 uppercase, 1 digit, 1 special char.");
        if (!isValidPhone(phone))
            throw new IllegalArgumentException("Invalid phone! Must be Egyptian format: 01XXXXXXXXX");
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future!");
        }
        for (Customer c : RestaurantDatabase.customers) {
            if (c.getUsername().equalsIgnoreCase(username))
                throw new IllegalArgumentException("Username already taken!");

        }
        Customer newCustomer = new Customer(id, username, password, dob, phone, 200.0, 0, dietary);
        RestaurantDatabase.customers.add(newCustomer);
        return newCustomer;
    }

    // --- Login ---
    public static Customer login(String username, String password) {
        for (Customer c : RestaurantDatabase.customers) {
            if (c.getUsername().equalsIgnoreCase(username) && c.getPassword().equals(password))
                return c;
        }
        return null;
    }

    // --- Reservations ---
    public List<Reservation> getMyReservations() {
        List<Reservation> mine = new ArrayList<>();
        for (Reservation r : RestaurantDatabase.reservations) {
            if (r.getCustomer().getId().equals(this.id) && !r.isCancelled())
                mine.add(r);
        }
        return mine;
    }

    public void cancelReservation(String reservationId) {
        for (Reservation r : RestaurantDatabase.reservations) {
            if (r.getReservationId().equals(reservationId) && r.getCustomer().getId().equals(this.id)) {
                r.cancel();
                return;
            }
        }
        throw new IllegalArgumentException("Reservation not found!");
    }

    // --- Orders ---
    public List<Order> getMyOrders() {
        List<Order> mine = new ArrayList<>();
        for (Order o : RestaurantDatabase.orders) {
            if (o.getCustomer().getId().equals(this.id))
                mine.add(o);
        }
        return mine;
    }

    // --- Getters / Setters ---
    public String getPhoneNumber() { return phoneNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void addLoyaltyPoints(int points) { this.loyaltyPoints += points; }
    public void deductLoyaltyPoints(int points) {
        if (points > loyaltyPoints) throw new IllegalArgumentException("Not enough loyalty points!");
        this.loyaltyPoints -= points;
    }
    public String getDietaryPreferences() { return dietaryPreferences; }
}
