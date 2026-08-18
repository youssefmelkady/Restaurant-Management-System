package com.restaurant.db;

import com.restaurant.billing.Invoice;
import com.restaurant.enums.TableStatus;
import com.restaurant.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabase {

    public static List<Customer>     customers    = new ArrayList<>();
    public static List<User>         staffMembers = new ArrayList<>();
    public static List<Table>        tables       = new ArrayList<>();
    public static List<MenuCategory> categories   = new ArrayList<>();
    public static List<MenuItem>     menuItems    = new ArrayList<>();
    public static List<Order>        orders       = new ArrayList<>();
    public static List<Reservation>  reservations = new ArrayList<>();
    public static List<Invoice>      invoices     = new ArrayList<>();

    static { seedDummyData(); }

    public static void seedDummyData() {

        Customer c1 = new Customer("C101", "customer1", "Pass@1234",
                LocalDate.of(2000, 1, 1), "01012345678", 500.0, 50, "None");
        Customer c2 = new Customer("C102", "ahmed123", "Ahmed@1234",
                LocalDate.of(2000, 3, 15), "01012345679", 1000.0, 100, "Vegetarian");
        customers.add(c1);
        customers.add(c2);

        Admin  admin  = new Admin ("S001", "admin",   "Admin@001", LocalDate.of(1990, 1, 1), 40);
        Waiter waiter = new Waiter("S002", "waiter1", "Waiter@01", LocalDate.of(1995, 5, 10), 35);
        staffMembers.add(admin);
        staffMembers.add(waiter);

        Table t1 = new Table("T1", 2, "Indoor",  TableStatus.AVAILABLE);
        Table t2 = new Table("T2", 4, "Outdoor", TableStatus.AVAILABLE);
        Table t3 = new Table("T3", 6, "VIP",     TableStatus.AVAILABLE);
        Table t4 = new Table("T4", 4, "Indoor",  TableStatus.AVAILABLE);
        Table t5 = new Table("T5", 8, "VIP",     TableStatus.AVAILABLE);
        tables.add(t1); tables.add(t2); tables.add(t3); tables.add(t4); tables.add(t5);

        MenuCategory appetizers = new MenuCategory("CAT1", "Appetizers",  "Starters & Snacks");
        MenuCategory mainCourse = new MenuCategory("CAT2", "Main Course", "Hearty Meals");
        MenuCategory beverages  = new MenuCategory("CAT3", "Beverages",   "Cold & Hot Drinks");
        MenuCategory desserts   = new MenuCategory("CAT4", "Desserts",    "Sweet treats");
        categories.add(appetizers); categories.add(mainCourse);
        categories.add(beverages);  categories.add(desserts);

        MenuItem m1 = new MenuItem("M1", "Garlic Bread",          25.0,  "Freshly baked with garlic butter",       appetizers, true);
        MenuItem m2 = new MenuItem("M2", "Grilled Chicken Pasta", 120.0, "Creamy Alfredo sauce with grilled chicken", mainCourse, true);
        MenuItem m3 = new MenuItem("M3", "Fresh Mango Juice",      35.0, "100% fresh natural mango",               beverages,  true);
        MenuItem m4 = new MenuItem("M4", "Steak Medium Rare",     220.0, "Prime beef steak",                       mainCourse, false);
        MenuItem m5 = new MenuItem("M5", "Chocolate Lava Cake",    60.0, "Warm chocolate cake with molten center", desserts,   true);
        MenuItem m6 = new MenuItem("M6", "Caesar Salad",           55.0, "Romaine, croutons, parmesan",            appetizers, true);
        menuItems.add(m1); menuItems.add(m2); menuItems.add(m3);
        menuItems.add(m4); menuItems.add(m5); menuItems.add(m6);

        Order initialOrder = new Order("ORD101", c1, t1);
        initialOrder.addItem(new OrderItem(m1, 2, "Extra Crispy"));
        initialOrder.addItem(new OrderItem(m2, 1, "Extra Cheese"));
        orders.add(initialOrder);
    }

    private static int nextNumericSuffix(List<?> items, java.util.function.Function<Object, String> idGetter) {
        int max = 0;
        for (Object item : items) {
            String id = idGetter.apply(item);
            if (id == null) continue;
            int i = id.length() - 1;
            while (i >= 0 && Character.isDigit(id.charAt(i))) i--;
            if (i < id.length() - 1) {
                try { max = Math.max(max, Integer.parseInt(id.substring(i + 1))); }
                catch (NumberFormatException ignored) { }
            }
        }
        return max + 1;
    }

    public static String nextCustomerId() {
        return "C" + String.format("%03d", nextNumericSuffix(customers, o -> ((Customer)o).getId()));
    }
    public static String nextReservationId() {
        return "R" + String.format("%03d", nextNumericSuffix(reservations, o -> ((Reservation)o).getReservationId()));
    }
    public static String nextOrderId() {
        return "ORD" + String.format("%03d", nextNumericSuffix(orders, o -> ((Order)o).getOrderId()));
    }
    public static String nextInvoiceId() {
        return "INV" + String.format("%03d", nextNumericSuffix(invoices, o -> ((Invoice)o).getInvoiceId()));
    }
    public static String nextMenuItemId() {
        return "M" + nextNumericSuffix(menuItems, o -> ((MenuItem)o).getItemId());
    }
    public static String nextTableId() {
        return "T" + nextNumericSuffix(tables, o -> ((Table)o).getTableNumber());
    }
}
