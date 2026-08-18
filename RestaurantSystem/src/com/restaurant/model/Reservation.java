package com.restaurant.model;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.enums.TableStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private String reservationId;
    private Customer customer;
    private Table table;
    private LocalDate date;
    private LocalTime timeSlot;
    private int partySize;
    private boolean cancelled;

    // Restaurant operating hours: 10:00 - 23:00
    private static final LocalTime OPEN  = LocalTime.of(10, 0);
    private static final LocalTime CLOSE = LocalTime.of(23, 0);

    public Reservation(String reservationId, Customer customer, Table table,
                       LocalDate date, LocalTime timeSlot, int partySize) {

        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Reservation date cannot be null");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("Reservation time cannot be null");
        }

        // Validation: party size
        if (partySize <= 0 || partySize > table.getCapacity())
            throw new IllegalArgumentException("Party size must be between 1 and " + table.getCapacity());

        // Validation: operating hours
        if (timeSlot.isBefore(OPEN) || !timeSlot.isBefore(CLOSE))
            throw new IllegalArgumentException("Reservations only accepted between 10:00 and 23:00");

        // Validation: no future past today
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Reservation date cannot be in the past!");

        // Validation: no double-booking (same table, same date, same time slot)
        for (Reservation r : RestaurantDatabase.reservations) {
            if (!r.isCancelled()
                    && r.getTable().getTableNumber().equals(table.getTableNumber())
                    && r.getDate().equals(date)
                    && r.getTimeSlot().equals(timeSlot)) {
                throw new IllegalArgumentException("Table " + table.getTableNumber() + " is already booked at this time!");
            }
        }

        this.reservationId = reservationId;
        this.customer     = customer;
        this.table        = table;
        this.date         = date;
        this.timeSlot     = timeSlot;
        this.partySize    = partySize;
        this.cancelled    = false;

        // A reservation belongs to a date/time slot, so it must not make
        // the table RESERVED for every other time slot.
    }

    public void cancel() {
        if (this.cancelled) return;
        this.cancelled = true;

        boolean anotherActiveReservation = RestaurantDatabase.reservations.stream()
                .anyMatch(r -> r != this
                        && !r.isCancelled()
                        && r.getTable().getTableNumber().equals(table.getTableNumber()));

        if (!anotherActiveReservation && this.table.getStatus() == TableStatus.RESERVED) {
            this.table.setStatus(TableStatus.AVAILABLE);
        }
    }

    public String getReservationId() { return reservationId; }
    public Customer getCustomer()    { return customer; }
    public Table getTable()          { return table; }
    public LocalDate getDate()       { return date; }
    public LocalTime getTimeSlot()   { return timeSlot; }
    public int getPartySize()        { return partySize; }
    public boolean isCancelled()     { return cancelled; }

    @Override
    public String toString() {
        return "#" + reservationId + " | Table " + table.getTableNumber()
                + " | " + date + " at " + timeSlot + " | Party of " + partySize
                + (cancelled ? " [CANCELLED]" : "");
    }
}
