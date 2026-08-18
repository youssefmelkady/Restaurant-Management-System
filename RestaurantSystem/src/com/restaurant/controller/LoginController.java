package com.restaurant.controller;

import com.restaurant.enums.Role;
import com.restaurant.model.Customer;
import com.restaurant.model.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton   rbCustomer;
    @FXML private RadioButton   rbStaff;
    @FXML private ToggleGroup   roleGroup;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.error("Login Error", "Please fill in all fields.");
            return;
        }

        if (rbCustomer.isSelected()) {
            Customer customer = Customer.login(username, password);
            if (customer == null) {
                AlertHelper.error("Login Failed", "Invalid username or password.");
                return;
            }
            SessionManager.getInstance().setCurrentUser(customer);
            SceneNavigator.navigateTo("customer_dashboard.fxml");
        } else {
            Staff staff = Staff.login(username, password);
            if (staff == null) {
                AlertHelper.error("Login Failed", "Invalid staff credentials.");
                return;
            }
            SessionManager.getInstance().setCurrentUser(staff);
            if (staff.getRole() == Role.ADMIN) {
                SceneNavigator.navigateTo("admin_dashboard.fxml");
            } else {
                SceneNavigator.navigateTo("waiter_dashboard.fxml");
            }
        }
    }

    @FXML
    public void handleGoRegister() {
        SceneNavigator.navigateTo("register.fxml");
    }
}
