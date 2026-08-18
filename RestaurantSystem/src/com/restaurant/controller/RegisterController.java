package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     phoneField;
    @FXML private DatePicker    dobPicker;
    @FXML private TextField     dietaryField;

    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String phone    = phoneField.getText().trim();
        String dietary  = dietaryField.getText().trim();
        LocalDate dob   = dobPicker.getValue();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || dob == null) {
            AlertHelper.error("Validation Error", "Please fill in all required fields.");
            return;
        }

        try {
            String id = RestaurantDatabase.nextCustomerId();
            Customer newCustomer = Customer.register(id, username, password, dob, phone, dietary.isEmpty() ? "None" : dietary);
            AlertHelper.info("Success", "Account created! Welcome, " + newCustomer.getUsername() + "!\nYou start with 200 EGP balance.");
            SceneNavigator.navigateTo("login.fxml");
        } catch (IllegalArgumentException e) {
            AlertHelper.error("Registration Failed", e.getMessage());
        }
    }

    @FXML
    public void handleBackToLogin() {
        SceneNavigator.navigateTo("login.fxml");
    }
}
