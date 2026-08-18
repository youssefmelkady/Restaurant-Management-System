package com.restaurant;

import com.restaurant.controller.SceneNavigator;
import com.restaurant.network.OrderStatusServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static OrderStatusServer server;

    @Override
    public void start(Stage primaryStage) {
        // Start order status server in background (BONUS)
        server = new OrderStatusServer();
        server.start();

        // Set up scene navigator
        SceneNavigator.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setResizable(true);

        // Start at login screen
        SceneNavigator.navigateTo("login.fxml");
    }

    @Override
    public void stop() {
        // Clean shutdown
        if (server != null) server.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
