package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.TableStatus;
import com.restaurant.model.Customer;
import com.restaurant.model.Reservation;
import com.restaurant.model.Table;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TableBrowsingController {

    @FXML private ComboBox<String>  locationFilter;
    @FXML private Spinner<Integer>  capacityFilter;
    @FXML private ListView<Table>   tableListView;
    @FXML private DatePicker        reservationDate;
    @FXML private TextField         timeSlotField;
    @FXML private Spinner<Integer>  partySizeSpinner;
    @FXML private Label             statusLabel;

    private ObservableList<Table> displayedTables = FXCollections.observableArrayList();
    private Task<List<Table>> currentLoadTask;

    @FXML
    public void initialize() {
        locationFilter.setItems(FXCollections.observableArrayList("All", "Indoor", "Outdoor", "VIP"));
        locationFilter.setValue("All");
        capacityFilter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        partySizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        tableListView.setItems(displayedTables);
        loadTablesAsync();
    }

    private void loadTablesAsync() {
        if (currentLoadTask != null && currentLoadTask.isRunning()) {
            currentLoadTask.cancel();
        }
        statusLabel.setText("Loading tables...");
        Task<List<Table>> task = new Task<>() {
            @Override
            protected List<Table> call() throws Exception {
                Thread.sleep(300);
                return RestaurantDatabase.tables;
            }
        };
        currentLoadTask = task;
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            applyFilters(task.getValue());
            statusLabel.setText(displayedTables.size() + " table(s) available.");
        }));
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void handleFilter() {
        applyFilters(RestaurantDatabase.tables);
    }

    @FXML
    public void handleRefresh() {
        loadTablesAsync();
    }

    private void applyFilters(List<Table> source) {
        String location = locationFilter.getValue();
        int minCap = capacityFilter.getValue();

        LocalDate selectedDate = reservationDate.getValue();
        LocalTime selectedTime = null;
        if (timeSlotField.getText() != null && !timeSlotField.getText().trim().isEmpty()) {
            try { selectedTime = LocalTime.parse(timeSlotField.getText().trim()); }
            catch (Exception ignored) { }
        }
        final LocalTime slot = selectedTime;

        List<Table> filtered = source.stream()
                .filter(t -> location.equals("All") || t.getLocation().equals(location))
                .filter(t -> t.getCapacity() >= minCap)
                .filter(t -> t.getStatus() != TableStatus.OCCUPIED)
                .filter(t -> selectedDate == null
                        || slot == null
                        || !hasReservationAt(t, selectedDate, slot))
                .collect(Collectors.toList());

        displayedTables.setAll(filtered);
    }

    private boolean hasReservationAt(Table table, LocalDate date, LocalTime time) {
        return RestaurantDatabase.reservations.stream()
                .anyMatch(r -> !r.isCancelled()
                        && r.getTable().getTableNumber().equals(table.getTableNumber())
                        && r.getDate().equals(date)
                        && r.getTimeSlot().equals(time));
    }

    @FXML
    public void handleReserve() {
        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            AlertHelper.error("Error", "Please log in first.");
            return;
        }

        Table selected = tableListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.error("Error", "Please select a table.");
            return;
        }

        LocalDate date = reservationDate.getValue();
        String timeText = timeSlotField.getText().trim();
        int partySize = partySizeSpinner.getValue();

        if (date == null || timeText.isEmpty()) {
            AlertHelper.error("Error", "Please enter a date and time (e.g. 19:00).");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeText);
            if (hasReservationAt(selected, date, time)) {
                throw new IllegalArgumentException("Table " + selected.getTableNumber() + " is already booked at this time!");
            }
            String id = RestaurantDatabase.nextReservationId();
            Reservation r = new Reservation(id, customer, selected, date, time, partySize);
            RestaurantDatabase.reservations.add(r);
            AlertHelper.info("Reserved", r.toString());
            loadTablesAsync();
        } catch (Exception e) {
            AlertHelper.error("Reservation Failed", e.getMessage());
        }
    }

    @FXML
    public void handleBack() {
        SceneNavigator.navigateTo("customer_dashboard.fxml");
    }
}
