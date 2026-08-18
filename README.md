# Restaurant Table Reservation and Ordering System

A desktop restaurant management application built in Java with JavaFX, developed as the major
project for **CSE241 – Object-Oriented Computer Programming** at Ain Shams University, Faculty
of Engineering (ICHEP).

Customers register, browse and reserve tables, order from a menu and pay an invoice. Staff sign
in through the same login screen and are routed to a different dashboard depending on their
role — an admin manages tables and menu items, while a waiter takes orders and updates their
status.

---

## Features

**Accounts and authentication**
- Customer registration validating password strength, phone format and date of birth
- One login screen for both customers and staff, routed by role to the correct dashboard
- Signed-in user carried across screens by a singleton `SessionManager`

**Tables and reservations**
- Browse tables with filtering by location, capacity and time slot
- Booking rules: party size against table capacity, operating hours (10:00–23:00), no past
  dates, and no double-booking of the same table, date and time slot
- View and cancel existing reservations

**Menu and orders**
- Browse the menu by category with a running order total
- Only items currently marked available can be added to an order
- Waiter screen moves an order through `PLACED → PREPARING → SERVED → PAID`, freeing the table
  once it is paid

**Billing**
- Invoice applying 14% tax and 12% service charge to the subtotal
- Four payment methods: cash, credit card, account balance and loyalty points

**Bonus features**
- **Multi-threading** — background table-availability refresh and asynchronous menu loading via
  JavaFX `Task`, with every UI update marshalled through `Platform.runLater`
- **Networking** — a `ServerSocket` order-status feed spawning a handler thread per client and
  broadcasting status changes to everyone connected

---

## Tech stack

| | |
|---|---|
| Language | Java 17+ |
| UI | JavaFX (SDK 26) with FXML |
| Styling | Single CSS stylesheet (168 lines) |
| Data | In-memory store using static `ArrayList` fields, seeded on launch |
| IDE | IntelliJ IDEA |

37 Java classes across 10 screens.

---

## Project structure

```
RestaurantSystem/
├── src/
│   ├── MainApp.java              # JavaFX entry point
│   └── com/restaurant/
│       ├── model/                # User, Customer, Staff, Admin, Waiter, Table,
│       │                         # Reservation, MenuItem, MenuCategory, Order, OrderItem
│       ├── enums/                # Role, TableStatus, OrderStatus, PaymentMethod
│       ├── interfaces/           # Payable, Manageable<T>
│       ├── db/                   # RestaurantDatabase — the in-memory store
│       ├── billing/              # Invoice, PaymentProcessor, CustomerAccount
│       ├── controller/           # 10 screen controllers, plus SessionManager,
│       │                         # SceneNavigator and AlertHelper
│       └── network/              # OrderStatusServer, OrderStatusClient
└── resources/
    ├── css/styles.css
    └── fxmls                     # 10 layout files
```

---

## Getting started

### Prerequisites

- JDK 17 or later
- [JavaFX SDK](https://gluonhq.com/products/javafx/) — developed against SDK 26
- IntelliJ IDEA, or any IDE that can attach an external library

### Setup in IntelliJ

1. Clone the repository and open the `RestaurantSystem` folder as a project.

2. **Attach the JavaFX library.**
   `File → Project Structure → Libraries → + → Java`, then select the `lib` folder inside your
   JavaFX SDK.

3. **Mark `resources/` as a Resources Root.**
   Right-click the folder → `Mark Directory as → Resources Root`. Without this,
   `getResource("/login.fxml")` returns `null` and the app fails on startup.

4. **Add the VM options.**
   `Run → Edit Configurations → VM options` — click *Modify options → Add VM options* if the
   field isn't shown. Paste the following, adjusting the path to your own SDK:

   ```
   --module-path C:\javafx-sdk-26.0.2\lib --add-modules javafx.controls,javafx.fxml
   ```

5. **Set the main class** to `com.restaurant.MainApp` and run.

> **Note:** `MainApp.java` sits at `src/MainApp.java` but declares `package com.restaurant`.
> IntelliJ flags the mismatch. Moving the file into `src/com/restaurant/` resolves it.

---

## Demo accounts

The data store is seeded on launch, so the application can be tested immediately.

| Role | Username | Password |
|---|---|---|
| Customer | `customer1` | `Pass@1234` |
| Customer | `ahmed123` | `Ahmed@1234` |
| Admin | `admin` | `Admin@001` |
| Waiter | `waiter1` | `Waiter@01` |

Select **Customer** or **Staff** on the login screen before signing in.

Five tables are seeded: T1 (2 seats, indoor), T2 (4, outdoor), T3 (6, VIP), T4 (4, indoor) and
T5 (8, VIP).

The order-status server listens on port **5001**. If that port is in use, change `PORT` in
`OrderStatusServer` and `OrderStatusClient`.

---

## Team

Work was split into four vertical slices, so each member owned a feature end to end — model
classes, GUI screens, FXML and their own section of the report.

| Member                   | Area                        |
|--------------------------|-----------------------------|
| Ali Atef (25p0009)       | Accounts and authentication |
| Yousef Sameh (25p0383)   | Tables and reservations     |
| Abdullah Osama (25p0103) | Menu and orders             |
| Youssef Elkady (2201500) | Billing and networking      |

---

## Known limitations

- Data lives in memory only and is lost when the application closes
- Passwords are stored as plain text rather than hashed
- The password validator requires an uppercase letter, a digit and a special character, but not
  a lowercase one — so `PASS1234!` is accepted
- Date-of-birth validation rejects future dates but enforces no minimum age
- Update operations exist for tables only, not for menu items or categories
- The order-status client is attached to the waiter dashboard rather than the customer
  dashboard, so the live feed does not yet reach the screen described in the specification
- Methods returning collections hand back the live list rather than a defensive copy

---

## License

Coursework submission for CSE241. Not licensed for reuse.
