package com.restaurant.gui;

import com.restaurant.enums.Role;
import com.restaurant.exceptions.ValidationException;
import com.restaurant.model.Customer;
import com.restaurant.model.User;
import com.restaurant.services.AuthService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = AuthService.login(username, password);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Welcome, " + user.getUsername() + "!");

            if (user.getRole() == Role.CUSTOMER) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                controller.setCustomer((Customer) user);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (ValidationException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {