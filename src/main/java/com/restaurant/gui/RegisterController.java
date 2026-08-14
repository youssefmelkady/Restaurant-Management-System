package com.restaurant.gui;

import com.restaurant.exceptions.ValidationException;
import com.restaurant.model.Customer;
import com.restaurant.services.AuthService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField dietaryField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        LocalDate dob = dobPicker.getValue();

        if (dob == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please select a date of birth.");
            return;
        }

        try {
            Customer created = AuthService.register(
                    usernameField.getText(),
                    passwordField.getText(),
                    dob,
                    phoneField.getText(),
                    dietaryField.getText()
            );
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Account created. Your ID is " + created.getId() + ".");
        } catch (ValidationException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Could not load login screen.");
        }
    }
}