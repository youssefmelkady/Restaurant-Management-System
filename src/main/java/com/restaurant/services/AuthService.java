package com.restaurant.services;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.exceptions.ValidationException;
import com.restaurant.model.Customer;
import com.restaurant.model.User;
import com.restaurant.utils.ValidationUtils;

import java.time.LocalDate;

public class AuthService {

    public static User login(String username, String password) throws ValidationException {
        for (Customer c : RestaurantDatabase.customers) {
            if (c.getUsername().equals(username) && c.getPassword().equals(password)) {
                return c;
            }
        }
        for (User s : RestaurantDatabase.staffMembers) {
            if (s.getUsername().equals(username) && s.getPassword().equals(password)) {
                return s;
            }
        }
        throw new ValidationException("Invalid username or password.");
    }

    public static Customer register(String username, String password, LocalDate dateOfBirth,
                                    String phoneNumber, String dietaryPreferences)
            throws ValidationException {

        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        for (Customer c : RestaurantDatabase.customers) {
            if (c.getUsername().equals(username)) {
                throw new ValidationException("Username is already taken.");
            }
        }

        ValidationUtils.validatePassword(password);
        ValidationUtils.validatePhoneNumber(phoneNumber);

        String newId = "C" + (101 + RestaurantDatabase.customers.size());
        Customer newCustomer = new Customer(newId, username, password, dateOfBirth,
                phoneNumber, 0.0, 0, dietaryPreferences);
        RestaurantDatabase.customers.add(newCustomer);
        return newCustomer;
    }
}