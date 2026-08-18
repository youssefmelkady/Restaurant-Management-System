package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.model.Staff;
import com.restaurant.model.User;


public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser()          { return currentUser; }

    public Customer getCurrentCustomer() {
        return (currentUser instanceof Customer) ? (Customer) currentUser : null;
    }

    public Staff getCurrentStaff() {
        return (currentUser instanceof Staff) ? (Staff) currentUser : null;
    }

    public void logout() { currentUser = null; }
}
